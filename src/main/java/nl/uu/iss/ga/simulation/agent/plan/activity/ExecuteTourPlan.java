package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.nl.uu.iss.ga.util.CumulativeDistribution.sampleWithCumulativeDistribution;
import static main.java.nl.uu.iss.ga.util.MNLModalChoiceModel.getChoiceProbabilities;
import static main.java.nl.uu.iss.ga.util.OutsideTripsDistribution.getOutsideTripsDistribution;

public class ExecuteTourPlan extends RunOncePlan<TripTour> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteTourPlan.class.getName());

    private final ActivityTour activityTour;
    private TripTour tripTour;
    private HashMap<TransportMode, Integer> travelTimeSeconds;
    private int nChangesBus;
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
        // initialise travel time
        this.travelTimeSeconds = new HashMap<TransportMode, Integer>();
        this.travelTimeSeconds.put(TransportMode.WALK, 0);
        this.travelTimeSeconds.put(TransportMode.BIKE, 0);
        this.travelTimeSeconds.put(TransportMode.CAR_DRIVER, 0);
        this.travelTimeSeconds.put(TransportMode.CAR_PASSENGER, 0);
        this.travelTimeSeconds.put(TransportMode.BUS_TRAM, 0);

        // initialise cost
        this.costs = new HashMap<TransportMode, Double>();
        this.costs.put(TransportMode.CAR_DRIVER, 3.0);
        this.costs.put(TransportMode.CAR_PASSENGER, 2.0);
        this.costs.put(TransportMode.BUS_TRAM, 2.0);

        this.nChangesBus = 0;

        Person person = planToAgentInterface.getContext(Person.class);
        boolean carPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();

        // create the trip tour from the activity tour
        this.generateTripTour(activityTour);

        // determine the modal choice for the tour
        if(this.tripTour.getTripChain().size() > 0) {
            // get choice probabilities from MNL model
            HashMap<TransportMode, Double> choiceProbabilities = getChoiceProbabilities(travelTimeSeconds, costs, nChangesBus, carPossible);

            // randomly sample a transport mode from the computed distribution
            TransportMode transportMode = sampleWithCumulativeDistribution(choiceProbabilities);

            //  select the travel time of the selected modal choice
            int travelTime = travelTimeSeconds.get(transportMode);

            this.tripTour.setTransportMode(transportMode);
            this.tripTour.setTravelTime(travelTime);
        }

        // just for printing for each trip in the chain
        LOGGER.log(Level.INFO, this.tripTour.toString());

        return this.tripTour;
    }

    /**
     * The function instantiate tripTour with the given tour/chain of activities
     * @param activityTour: chain of activities
     */
    private void generateTripTour(ActivityTour activityTour){
        // ------------------------------------------------------------------------------

        List<Activity> activities = activityTour.getActivityTour();

        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Activity activityOrigin = activities.get(0);

        boolean lastTripByTrain = false;

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            boolean tripInDHZW = true;

            // The simulation is only for trips that have origin and/or arrival in DHZW

            if (activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW()) {
                // entirely inside DHZW

                // todo: update compute here the time of the trip per each modal choice
                this.travelTimeSeconds.put(TransportMode.WALK, this.travelTimeSeconds.get(TransportMode.WALK) + 45);
                this.travelTimeSeconds.put(TransportMode.BIKE, this.travelTimeSeconds.get(TransportMode.WALK) + 30);
                this.travelTimeSeconds.put(TransportMode.CAR_DRIVER, this.travelTimeSeconds.get(TransportMode.WALK) + 15);
                this.travelTimeSeconds.put(TransportMode.CAR_PASSENGER, this.travelTimeSeconds.get(TransportMode.WALK) + 15);
                this.travelTimeSeconds.put(TransportMode.BUS_TRAM, this.travelTimeSeconds.get(TransportMode.WALK) + 25);

                tripInDHZW = true;

            } else if (activityOrigin.getLocation().isInsideDHZW() & !activityDestination.getLocation().isInsideDHZW()) {
                // inside -> outside

                HashMap<TransportMode, Double> outsideProbabilities = getOutsideTripsDistribution(activityDestination.getActivityType());

                // randomly sample a transport mode from the computed distribution
                TransportMode outsideTransportMode = sampleWithCumulativeDistribution(outsideProbabilities);

                if(outsideTransportMode.equals(TransportMode.TRAIN)){
                    if(Math.random() < 0.15) {
                        // the agent goes to The Hague Moerwijk to take the train. Hence, I need to care about such internal trip
                        lastTripByTrain = true;

                        activityDestination.getLocation().setToTrainStation();

                        // todo: update compute here the time of the trip per each modal choice
                        this.travelTimeSeconds.put(TransportMode.WALK, this.travelTimeSeconds.get(TransportMode.WALK) + 45);
                        this.travelTimeSeconds.put(TransportMode.BIKE, this.travelTimeSeconds.get(TransportMode.WALK) + 30);
                        this.travelTimeSeconds.put(TransportMode.CAR_DRIVER, this.travelTimeSeconds.get(TransportMode.WALK) + 15);
                        this.travelTimeSeconds.put(TransportMode.CAR_PASSENGER, this.travelTimeSeconds.get(TransportMode.WALK) + 15);
                        this.travelTimeSeconds.put(TransportMode.BUS_TRAM, this.travelTimeSeconds.get(TransportMode.WALK) + 25);

                        tripInDHZW = true;
                    }
                } else {
                    // the main trip is not by train, though Moerwijk, so I do not care
                    tripInDHZW = false;
                }
            } else if (!activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW() & lastTripByTrain) {
                // the agent comes back from outside, and before it went out by train
                lastTripByTrain = false;

                activityOrigin.getLocation().setToTrainStation();

                // todo: update compute here the time of the trip per each modal choice
                travelTimeSeconds.put(TransportMode.WALK, travelTimeSeconds.get(TransportMode.WALK) + 45);
                travelTimeSeconds.put(TransportMode.BIKE, travelTimeSeconds.get(TransportMode.WALK) + 30);
                travelTimeSeconds.put(TransportMode.CAR_DRIVER, travelTimeSeconds.get(TransportMode.WALK) + 15);
                travelTimeSeconds.put(TransportMode.CAR_PASSENGER, travelTimeSeconds.get(TransportMode.WALK) + 15);
                travelTimeSeconds.put(TransportMode.BUS_TRAM, travelTimeSeconds.get(TransportMode.WALK) + 25);

                tripInDHZW = true;

            } else {
                // outside -> outside, outside -> inside without the train to go outside before
                tripInDHZW = false;
            }

            // add the trip to the chain
            if (tripInDHZW) {
                this.tripTour.addTrip(new Trip(activityOrigin.getPid(),
                        activityOrigin.getHid(),
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType(),
                        activityOrigin.getLocation().getPc4(),
                        activityDestination.getLocation().getPc4(),
                        activityOrigin.getLocation().getLocationID(),
                        activityDestination.getLocation().getLocationID(),
                        activityOrigin.getStartTime(),
                        activityOrigin.getEndTime(),
                        activityDestination.getStartTime()
                ));
            }

            // update past activity
            activityOrigin = activityDestination;
        }
    }


}
