package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.LocationEntry;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import main.java.nl.uu.iss.ga.util.OutsideTripsDistribution;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.nl.uu.iss.ga.util.CumulativeDistribution.sampleWithCumulativeDistribution;
import static main.java.nl.uu.iss.ga.util.MNLModalChoiceModel.getChoiceProbabilities;
import static main.java.nl.uu.iss.ga.util.OutsideTripsDistribution.getOutsideTripsDistribution;

public class ExecuteChainTripPlan extends RunOncePlan<TripChain> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteChainTripPlan.class.getName());

    private final ActivityChain activityChain;
    private TripChain tripChain;

    public ExecuteChainTripPlan(ActivityChain activityChain) {
        this.activityChain = activityChain;
    }

    /**
     * This function is called at every midnight for each action of the next day.
     *
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripChain executeOnce(PlanToAgentInterface<TripChain> planToAgentInterface) {
        // ------------------------------------------------------------------------------
        // part to retrieve from reality

        // retrieve times
        HashMap<TransportMode, Integer> travelTimeSeconds = new HashMap<TransportMode, Integer>();
        travelTimeSeconds.put(TransportMode.WALK, 45);
        travelTimeSeconds.put(TransportMode.BIKE, 30);
        travelTimeSeconds.put(TransportMode.CAR_DRIVER, 15);
        travelTimeSeconds.put(TransportMode.CAR_PASSENGER, 15);
        travelTimeSeconds.put(TransportMode.BUS_TRAM, 25);

        // retrieve costs
        HashMap<TransportMode, Double> costs = new HashMap<TransportMode, Double>();
        costs.put(TransportMode.CAR_DRIVER, 3.0);
        costs.put(TransportMode.CAR_PASSENGER, 2.0);
        costs.put(TransportMode.BUS_TRAM, 2.0);

        int nChangesBus = 0;
        // ------------------------------------------------------------------------------

        Person person = planToAgentInterface.getContext(Person.class);

        List<Activity> activities = activityChain.getActivityChain();

        this.tripChain = new TripChain(activityChain.getPid(), activityChain.getDay());
        TransportMode transportMode = null;
        Activity activityOrigin = activities.get(0);

        // todo: to be modified if the agent is in a location without the car/bike
        boolean carPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();
        boolean bikePossible = true;

        boolean lastTripByTrain = false;

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            boolean tripInDHZW = true;

            // The simulation is only for trips that have origin and/or arrival in DHZW
            ActivityTime departureTime = null;
            HashMap<TransportMode, Double> choiceProbabilities = null;
            HashMap<TransportMode, Double> outsideProbabilities = null;
            Integer travelTime = 0;

            if (activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW()) {
                // entirely inside DHZW
                // todo: compute here the time of the trip per each modal choice

                // get choice probabilities from MNL model
                choiceProbabilities = getChoiceProbabilities(travelTimeSeconds, costs, nChangesBus, carPossible, bikePossible);

                // randomly sample a transport mode from the computed distribution
                transportMode = sampleWithCumulativeDistribution(choiceProbabilities);

                //  select the travel time of the selected modal choice
                travelTime = travelTimeSeconds.get(transportMode);

                tripInDHZW = true;

            } else if (activityOrigin.getLocation().isInsideDHZW() & !activityDestination.getLocation().isInsideDHZW()) {
                // inside -> outside

                outsideProbabilities = getOutsideTripsDistribution(activityDestination.getActivityType());

                // randomly sample a transport mode from the computed distribution
                transportMode = sampleWithCumulativeDistribution(outsideProbabilities);

                if(transportMode.equals(TransportMode.TRAIN)){
                    if(Math.random() < 15) {
                        // the agent goes to The Hague Moerwijk to take the train. Hence, I need to care about such internal trip
                        lastTripByTrain = true;

                        activityDestination.getLocation().setToTrainStation();

                        // get choice probabilities from MNL model
                        choiceProbabilities = getChoiceProbabilities(travelTimeSeconds, costs, nChangesBus, carPossible, bikePossible);

                        // randomly sample a transport mode from the computed distribution
                        transportMode = sampleWithCumulativeDistribution(choiceProbabilities);

                        //  select the travel time of the selected modal choice
                        travelTime = travelTimeSeconds.get(transportMode);

                        tripInDHZW = true;
                    }
                } else {
                    // the main trip is not by train, though Moerwijk, so I do not care
                    tripInDHZW = false;
                }
            } else if (!activityOrigin.getLocation().isInsideDHZW() & activityDestination.getLocation().isInsideDHZW() & lastTripByTrain) {
                // to be decided... if it is again about the loop thing

                lastTripByTrain = false;
                tripInDHZW = true;

            } else {
                // outside -> outside, outside -> inside without the train to go outside before
                tripInDHZW = false;
            }


            // add the trip to the chain
            if (tripInDHZW) {
                this.tripChain.addTrip(new Trip(activityOrigin.getPid(),
                        activityOrigin.getHid(),
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType(),
                        activityOrigin.getLocation().getPc4(),
                        activityDestination.getLocation().getPc4(),
                        activityOrigin.getLocation().getLocationID(),
                        activityDestination.getLocation().getLocationID(),
                        activityOrigin.getEndTime(),
                        activityDestination.getStartTime(),
                        transportMode,
                        travelTime
                ));
            }

            // update past activity
            activityOrigin = activityDestination;
        }

        // just for printing for each trip in the chain
        for (Trip t : this.tripChain.getTripChain()) {
            LOGGER.log(Level.INFO, t.getDepartureActivityType() + ", " + t.getDepartureLid() + " (finishes at " + t.getPreviousActivityEndTime() + ") -> " + t.getArrivalActivityType() + ", " + t.getArrivalLid() + " (starts at " + t.getNextActivityStartTime() + ") - " + t.getTransportMode().getStringCode());
        }

        return this.tripChain;
    }
}
