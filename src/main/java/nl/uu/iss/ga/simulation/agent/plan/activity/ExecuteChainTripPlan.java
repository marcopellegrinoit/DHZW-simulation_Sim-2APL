package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        if (activityChain.getPid() == 84) {
            LOGGER.log(Level.INFO, "GoalPlanScheme - " + activityChain.getDay() + " - " + activityChain.getActivityChain().size());
        }

        List<Activity> activities = activityChain.getActivityChain();

        this.tripChain = new TripChain(activityChain.getPid(), activityChain.getDay());

        Activity activityOrigin = activities.get(0);
        for (Activity activityDestination : activities.subList(1, activities.size())) {

            // decide time and modal choice
            ActivityTime departureTime = new ActivityTime(0);
            ActivityTime arrivalTime = new ActivityTime(0);

            TransportMode transportMode;

            // some stupid differentiation to see if works
            if (activityOrigin.getStartTime().getSeconds() == 0) {
                transportMode = TransportMode.BUS;
            } else {
                transportMode = TransportMode.BICYCLE;
            }

            // add the trip to the chain
            this.tripChain.addTrip(new Trip(activityOrigin.getPid(),
                                    activityOrigin.getHid(),
                                    activityDestination.getActivityType(),
                                    activityOrigin.getLocation().getPc4(),
                                    activityDestination.getLocation().getPc4(),
                                    activityOrigin.getStartTime(),
                                    activityDestination.getStartTime(),
                                    departureTime,
                                    arrivalTime,
                                    transportMode
            ));
            activityOrigin = activityDestination; // update past activity
        }

        if (person.getPid() == 84) {
            LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + person.getPid() + " - " + this.tripChain.getTripChain().size());
        }

        // just for printing for each trip in the chain
        for (Trip trip : this.tripChain.getTripChain()) {
            if (person.getPid() == 84) {
                LOGGER.log(Level.INFO,trip.getPreviousActivityTime() + " - " + trip.getNextActivityTime() + " - " + trip.getTransportMode().getStringCode());
            }
        }

        return this.tripChain;
    }
}
