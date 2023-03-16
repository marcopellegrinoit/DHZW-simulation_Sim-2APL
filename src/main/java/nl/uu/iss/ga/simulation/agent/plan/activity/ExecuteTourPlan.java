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
    private HashMap<TransportMode, Double> travelTimes;
    private HashMap<TransportMode, Double> travelDistances;
    private int nChangesBus;
    private int nChangesTrain;
    private int walkTimeBus;
    private int walkTimeTrain;

    private boolean trainPossible;
    private boolean busPossible;
    private boolean walkPossible;
    private boolean bikePossible ;
    private boolean carDriverPossible;
    private boolean carPassengerPossible;

    private final long pid;
    private final long hid;

    public ExecuteTourPlan(ActivityTour activityTour) {
        this.activityTour = activityTour;
        this.pid = activityTour.getPid();
        this.hid = activityTour.getHid();
    }

    /**
     * This function is called at every midnight for each action of the next day.
     *
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripTour executeOnce(PlanToAgentInterface<TripTour> planToAgentInterface) {
        this.travelTimes = new HashMap<TransportMode, Double>();
        this.travelDistances = new HashMap<TransportMode, Double>();
        this.walkTimeBus = 0;
        this.walkTimeTrain = 0;

        Person person = planToAgentInterface.getContext(Person.class);

        /**
         *  generation of transport mode for each trip
         */
        List<Activity> activities = activityTour.getActivityTour();

        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Activity activityOrigin = activities.get(0);

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            //todo change to true
            this.trainPossible = false;
            this.carDriverPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();
            this.nChangesBus = 0;
            this.nChangesTrain = 0;
            this.travelTimes.clear();
            this.travelDistances.clear();

            // not entirely outside DHZW
            if (activityOrigin.getLocation().isInDHZW() & activityDestination.getLocation().isInDHZW()) {
//            if (activityOrigin.getLocation().isInsideDHZW() | activityDestination.getLocation().isInsideDHZW()) {
                // calculate the time for:
                // car only
                // bike only
                // foot only

                // calculate time and distance for car
                // apply it to car passenger
                if(this.carDriverPossible) {
                    // apply it for car driver
                }

                // if it is possible by bus, calculate the time
                //  travelTimes.put(TransportMode.BUS_TRAM, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.BUS_TRAM));
                // else
                //  busPossible = false;

                // if the trip is partially outside, the train is an option
                if (!(activityOrigin.getLocation().isInDHZW() & activityDestination.getLocation().isInDHZW())) {
                    // calculate the time for:
                    // train + walk + bike

                    // if it is possible by train
                    //  calculate the time
                    //  travelTimes.put(TransportMode.TRAIN, calculateTravelTime(activityOrigin.getLocation(), activityDestination.getLocation(), TransportMode.TRAIN));
                    // else
                    //  trainPossible = false;
                }

                // compute choice probabilities
                HashMap<TransportMode, Double> choiceProbabilities = MNLModalChoiceModel.getChoiceProbabilities(this.travelTimes, this.travelDistances, this.nChangesBus, this.nChangesTrain, this.carDriverPossible, this.carPassengerPossible, this.trainPossible, this.busPossible, this.walkPossible, this.bikePossible, this.walkTimeBus, this.walkTimeTrain, activityOrigin.getActivityType(), activityDestination.getActivityType());

                // decide the modal choice
                TransportMode transportMode = CumulativeDistribution.sampleWithCumulativeDistribution(choiceProbabilities);

                double travelTime = travelTimes.get(transportMode);
                double travelDistance = travelDistances.get(transportMode);

                // add the trip to the tour
                Trip trip = new Trip(this.pid,
                        this.hid,
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType(),
                        activityOrigin.getLocation().getPostcode(),
                        activityDestination.getLocation().getPostcode(),
                        activityOrigin.getStartTime(),
                        activityOrigin.getEndTime(),
                        activityDestination.getStartTime(),
                        transportMode,
                        travelTime,
                        travelDistance);

                this.tripTour.addTrip(trip);

                LOGGER.log(Level.INFO, trip.toString());
            }
            // update past activity
            activityOrigin = activityDestination;
        }

        return this.tripTour;
    }

}
