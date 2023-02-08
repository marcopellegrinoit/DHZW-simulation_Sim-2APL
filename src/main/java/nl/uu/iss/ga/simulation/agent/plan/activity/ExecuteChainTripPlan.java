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

public class ExecuteChainTripPlan extends RunOncePlan<TripChain> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteChainTripPlan.class.getName());

    private final ActivityChain activityChain;
    private TripChain tripChain;

    public ExecuteChainTripPlan(ActivityChain activityChain) {
        this.activityChain = activityChain;
    }

    /**
     * This function is called at every midnight for each action of the next day.
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripChain executeOnce(PlanToAgentInterface<TripChain> planToAgentInterface) {
        Person person = planToAgentInterface.getContext(Person.class);

        List<Activity> activities = activityChain.getActivityChain();

        this.tripChain = new TripChain(activityChain.getPid(), activityChain.getDay());

        Activity activityOrigin = activities.get(0);

        if (person.getPid() == 34271) {
            LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + person.getPid() + " - " + activities.size());
        }

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {

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


            // todo: to be modified if the agent is in a location without the car/bike
            boolean carPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();
            boolean bikePossible = true;

            // get choice probabilities from MNL model
            HashMap<TransportMode, Double> choiceProbabilities = getChoiceProbabilities(travelTimeSeconds, costs, nChangesBus, carPossible, bikePossible);

            // randomly sample a transport mode from the computed distribution
            TransportMode transportMode = sampleWithCumulativeDistribution(choiceProbabilities);

            //  select the travel time of the selected modal choice
            int tripDurationSeconds = travelTimeSeconds.get(transportMode);
            ActivityTime departureTime = new ActivityTime(activityDestination.getStartTime().getSeconds() - tripDurationSeconds);

            // add the trip to the chain
            this.tripChain.addTrip(new Trip(activityOrigin.getPid(),
                                    activityOrigin.getHid(),
                                    activityOrigin.getActivityType(),
                                    activityDestination.getActivityType(),
                                    activityOrigin.getLocation().getPc4(),
                                    activityDestination.getLocation().getPc4(),
                                    activityOrigin.getStartTime(),
                                    activityDestination.getStartTime(),
                                    departureTime,
                                    activityDestination.getStartTime(), // the trip arrives when the next activity starts
                                    transportMode
            ));

            // update past activity
            activityOrigin = activityDestination;

            // print the choice probabilities
            if (person.getPid() == 45049) {
                LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + choiceProbabilities);
            }
        }

        if (person.getPid() == 45049) {
            // just for printing for each trip in the chain
            for (Trip trip : this.tripChain.getTripChain()) {
                LOGGER.log(Level.INFO,trip.getDepartureActivityType() +"("+ trip.getPreviousActivityTime() + ") -> " + trip.getArrivalActivityType() +"("+ trip.getNextActivityTime() + ") - " + trip.getTransportMode().getStringCode());
            }

        }


        return this.tripChain;
    }
}
