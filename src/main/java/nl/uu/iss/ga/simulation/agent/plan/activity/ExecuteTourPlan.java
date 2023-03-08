package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.LocationEntry;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import main.java.nl.uu.iss.ga.util.CumulativeDistribution;
import main.java.nl.uu.iss.ga.util.MNLModalChoiceModel;
import main.java.nl.uu.iss.ga.util.SortBasedOnMessageId;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExecuteTourPlan extends RunOncePlan<TripTour> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteTourPlan.class.getName());
    private final ActivityTour activityTour;
    private TripTour tripTour;
    private HashMap<TransportMode, Integer> travelTimes;
    private HashMap<TransportMode, Integer> travelDistances;

    private int nChangesBus;
    private int nChangesTrain;
    private HashMap<TransportMode, Double> costs;    // todo. improvement: update cost of fuel based on trip

    public ExecuteTourPlan(ActivityTour activityTour) {
        this.activityTour = activityTour;
    }

    /**
     * This function is called at every midnight for each action of the next day.
     *
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripTour executeOnce(PlanToAgentInterface<TripTour> planToAgentInterface) {
        this.travelTimes = new HashMap<TransportMode, Integer>();
        this.travelDistances = new HashMap<TransportMode, Integer>();

        // initialise cost
        this.costs = new HashMap<TransportMode, Double>();
        this.costs.put(TransportMode.CAR_DRIVER, 3.0);
        this.costs.put(TransportMode.CAR_PASSENGER, 2.0);
        this.costs.put(TransportMode.BUS_TRAM, 2.0);

        Person person = planToAgentInterface.getContext(Person.class);
        boolean carPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();

        /**
         *  generation of transport mode for each trip
         */
        List<Activity> activities = activityTour.getActivityTour();

        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Activity activityOrigin = activities.get(0);

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            //todo change to true
            boolean trainPossible = false;
            boolean busPossible = false;
            this.nChangesBus = 0;
            this.nChangesTrain = 0;
            this.travelTimes.clear();
            this.travelDistances.clear();

            // not entirely outside DHZW
            if (activityOrigin.getLocation().isInsideDHZW() | activityDestination.getLocation().isInsideDHZW()) {
                // calculate the time for:
                // car only
                // bike only
                // foot only
                calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.WALK);
                calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BIKE);
                calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.CAR_PASSENGER);
                if(carPossible) {
                    calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.CAR_DRIVER);
                }

                // if it is possible by bus
                //  calculate the time
                //  travelTimes.put(TransportMode.BUS_TRAM, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BUS_TRAM));
                // else
                //  busPossible = false;

                // if the trip is partially outside, the train is an option
                if (!(activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW())) {
                    // calculate the time for:
                    // train + walk + bike

                    // if it is possible by train
                    //  calculate the time
                    //  travelTimes.put(TransportMode.TRAIN, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.TRAIN));
                    // else
                    //  trainPossible = false;
                }

                // compute choice probabilities
                HashMap<TransportMode, Double> choiceProbabilities = MNLModalChoiceModel.getChoiceProbabilities(travelTimes, costs, nChangesBus, nChangesTrain, carPossible,trainPossible, busPossible);

                // decide the modal choice
                TransportMode transportMode = CumulativeDistribution.sampleWithCumulativeDistribution(choiceProbabilities);

                // if the trip is partially outside and by bus or train
                if (!(activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW()) & (transportMode.equals(TransportMode.TRAIN) | transportMode.equals(TransportMode.BUS_TRAM))) {
                    // todo
                    // find the bus or train station.
                    // set it as destination, or stating point
                    // recalculate the modal choice, choosing from: walk, bike, car_passenger, car_driver, bus_tram
                }

                int travelTime = travelTimes.get(transportMode);
                int travelDistance = travelDistances.get(transportMode);

                // add the trip to the tour
                Trip trip = new Trip(activityOrigin.getPid(),
                        activityOrigin.getHid(),
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType(),
                        activityOrigin.getLocation().getPc4(),
                        activityDestination.getLocation().getPc4(),
                        activityOrigin.getLocation().getLocationID(),
                        activityDestination.getLocation().getLocationID(),
                        activityOrigin.getStartTime(),
                        activityOrigin.getEndTime(),
                        activityDestination.getStartTime(),
                        transportMode,
                        travelTime,
                        travelDistance);
                LOGGER.log(Level.INFO, trip.toString());
                this.tripTour.addTrip(trip);


            }
            // update past activity
            activityOrigin = activityDestination;
        }

        return this.tripTour;
    }

    private void calculateTimeDistance (LocationEntry departure, LocationEntry arrival, TransportMode transportMode) {
        // fixed flags
        String otpBaseUrl = "http://localhost:8801/otp/routers/default/";
        String date = "2023-03-06";
        int numberResults = 2;
        boolean arriveBy = true;

        String fromPoint = departure.getLatitude() +","+ departure.getLongitude();
        String toPoint = arrival.getLatitude() +","+ arrival.getLongitude();
        String time = arrival.getStartTime().getHour_of_day() + ":" + arrival.getStartTime().getMinute_of_hour();

        String mode = null;
        switch (transportMode){
            case WALK:
                mode = "WALK";
                break;
            case BIKE:
                mode = "BICYCLE";
                break;
            case CAR_DRIVER:
            case CAR_PASSENGER:
                mode = "CAR";
                break;
            case BUS_TRAM:
                mode = "WALK,BICYCLE,BUS,TRAM";
                break;
            case TRAIN:
                mode = "WALK,BICYCLE,CAR,TRAIN";
                break;
        }

        // create the full query
        String query = otpBaseUrl + "plan?fromPlace=" + fromPoint + "&toPlace=" + toPoint + "&date=" + date + "&time=" + time + "&arriveBy=" + arriveBy + "&numItineraries=" + numberResults + "&mode=" + mode;

        int travelTime = 0;
        try {
            URL url = new URL(query);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the OTP server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject responseJSON = new JSONObject(response.toString());
            JSONArray jsonItineraries = responseJSON.getJSONObject("plan").getJSONArray("itineraries");

            // order for the fastest connection
            // todo: instead of of ordering, just filter the one that contains the modal choice
            List<JSONObject> list = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonItineraries.length(); i++) {
                list.add(jsonItineraries.getJSONObject(i));
            }
            list.sort(new SortBasedOnMessageId());

            JSONObject fastestItinerary = new JSONArray(list).getJSONObject(0);

            travelTime = fastestItinerary.getInt("duration");

            //todo
            // distance

            System.out.println(responseJSON);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.log(Level.INFO, transportMode + ": " + travelTime);

        this.travelTimes.put(transportMode, travelTime/60);
        this.travelDistances.put(transportMode, 0);
    }



}
