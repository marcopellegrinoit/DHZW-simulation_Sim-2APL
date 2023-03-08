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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExecuteTourPlan extends RunOncePlan<TripTour> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteTourPlan.class.getName());
    private final ActivityTour activityTour;
    private TripTour tripTour;
    private HashMap<TransportMode, Integer> travelTimes;
    private HashMap<TransportMode, Double> travelDistances;

    private int nChangesBus;
    private int nChangesTrain;
    private int walkTimeBus;
    private int walkTimeTrain;
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
        this.travelDistances = new HashMap<TransportMode, Double>();
        this.walkTimeBus = 0;
        this.walkTimeTrain = 0;

        // initialise cost
        this.costs = new HashMap<TransportMode, Double>();
        this.costs.put(TransportMode.CAR_DRIVER, 3.0);
        this.costs.put(TransportMode.CAR_PASSENGER, 2.0);
        this.costs.put(TransportMode.BUS_TRAM, 2.0);

        Person person = planToAgentInterface.getContext(Person.class);

        /**
         *  generation of transport mode for each trip
         */
        List<Activity> activities = activityTour.getActivityTour();

        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Activity activityOrigin = activities.get(0);

        boolean trainPossible;
        boolean busPossible;
        boolean walkPossible;
        boolean bikePossible ;
        boolean carDriverPossible;
        boolean carPassengerPossible;

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            //todo change to true
            trainPossible = false;
            carDriverPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();
            this.nChangesBus = 0;
            this.nChangesTrain = 0;
            this.travelTimes.clear();
            this.travelDistances.clear();

            // not entirely outside DHZW
            if (activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW()) {
//            if (activityOrigin.getLocation().isInsideDHZW() | activityDestination.getLocation().isInsideDHZW()) {
                // calculate the time for:
                // car only
                // bike only
                // foot only
                walkPossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.WALK);
                bikePossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BIKE);
                if(carDriverPossible) {
                    carDriverPossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.CAR_DRIVER);
                }
                carPassengerPossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.CAR_PASSENGER);

                // if it is possible by bus
                //  calculate the time
                //  travelTimes.put(TransportMode.BUS_TRAM, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BUS_TRAM));
                // else
                //  busPossible = false;
                busPossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BUS_TRAM);

                // if the trip is partially outside, the train is an option
                if (!(activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW())) {
                    System.out.println("ERROR FOR NOW");
                    // calculate the time for:
                    // train + walk + bike

                    // if it is possible by train
                    //  calculate the time
                    //  travelTimes.put(TransportMode.TRAIN, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.TRAIN));
                    // else
                    //  trainPossible = false;
                    trainPossible = calculateTimeDistance(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.TRAIN);
                }

                // compute choice probabilities
                HashMap<TransportMode, Double> choiceProbabilities = MNLModalChoiceModel.getChoiceProbabilities(travelTimes, costs, nChangesBus, nChangesTrain, carDriverPossible, carPassengerPossible,trainPossible, busPossible, walkPossible, bikePossible, this.walkTimeBus, this.walkTimeTrain);

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
                double travelDistance = travelDistances.get(transportMode);

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

    private boolean calculateTimeDistance (LocationEntry departure, LocationEntry arrival, TransportMode transportMode) {
        // fixed flags
        String otpBaseUrl = "http://localhost:8801/otp/routers/default/";
        String date = "2023-03-06";
        int numberResults = 3;
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
                mode = "WALK,BUS,TRAM,SUBWAY";
                break;
            case TRAIN:
                mode = "WALK,BICYCLE,TRAIN,TRAM,BUS,SUBWAY,CAR";
                break;
        }

        //LOGGER.log(Level.INFO, "Looking from " + fromPoint + " to " + toPoint + " arrive at " + time + " mode:" + mode);


        // create the full query
        String query = otpBaseUrl + "plan?fromPlace=" + fromPoint + "&toPlace=" + toPoint + "&date=" + date + "&time=" + time + "&arriveBy=" + arriveBy + "&numItineraries=" + numberResults + "&mode=" + mode;

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

            // filter the results by the one that really contains the desired mode choice
            List<JSONObject> filteredItineraries = new ArrayList<>();
            for (int i = 0; i < jsonItineraries.length(); i++) {
                JSONObject itinerary = jsonItineraries.getJSONObject(i);
                JSONArray legs = itinerary.getJSONArray("legs");
                boolean containsMode = false;
                for (int j = 0; j < legs.length(); j++) {
                    JSONObject leg = legs.getJSONObject(j);
                    if(transportMode.equals(TransportMode.BUS_TRAM)){
                        if (leg.getString("mode").equals("BUS") | leg.getString("mode").equals("TRAM")) {
                            containsMode = true;
                            break;
                        }
                    } else if (transportMode.equals(TransportMode.TRAIN)) {
                        if (leg.getString("mode").equals("TRAIN")) {
                            containsMode = true;
                            break;
                        }
                    } else {
                        if (leg.getString("mode").equals(mode)) {
                            containsMode = true;
                            break;
                        }
                    }
                }
                if (containsMode) {
                    filteredItineraries.add(itinerary);
                }
            }

            // if there is at least one suitable result
            if(filteredItineraries.size() > 0) {

                // Sort the filtered itineraries by duration
                filteredItineraries.sort(new Comparator<JSONObject>() {
                    public int compare(JSONObject o1, JSONObject o2) {
                        return Integer.compare(o1.getInt("duration"), o2.getInt("duration"));
                    }
                });

                // Get the first itinerary from the filtered list
                JSONObject fastestItinerary = filteredItineraries.get(0);

                // calculate travel time in minutes
                int travelTime = fastestItinerary.getInt("duration")/60;

                // calculate distance and walk time
                double distance = 0;
                int walkTime = 0;
                JSONArray legs = fastestItinerary.getJSONArray("legs");
                for (int i = 0; i < legs.length(); i++) {
                    JSONObject leg = legs.getJSONObject(i);
                    distance += leg.getInt("distance");

                    if(leg.getString("mode").equals("WALK")){
                        walkTime += leg.getInt("duration");
                    }
                }
                distance = distance/1000; // transform from meters to kilometers

                // calculate number of changes. Subtract 3 because I do not want to count the going to and from the station. Those are not changes.
                if(transportMode.equals(TransportMode.TRAIN)){
                    this.nChangesTrain = legs.length()-3;
                    this.walkTimeTrain = walkTime;
                } else if (transportMode.equals(TransportMode.BUS_TRAM)){
                    this.nChangesBus = legs.length()-3;
                    this.walkTimeBus = walkTime;
                }

                LOGGER.log(Level.INFO, transportMode + ". Time: " + travelTime + " mins - distance: " + distance + " km");

                this.travelTimes.put(transportMode, travelTime);
                this.travelDistances.put(transportMode, distance);

                return(true);
            } else {
                LOGGER.log(Level.INFO, transportMode + ". No solution available");

                return(false);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
