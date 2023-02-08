package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.ArrayList;
import java.util.HashMap;
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

        List<Activity> activities = activityChain.getActivityChain();

        this.tripChain = new TripChain(activityChain.getPid(), activityChain.getDay());

        Activity activityOrigin = activities.get(0);

        if (person.getPid() == 34271) {
            LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + person.getPid() + " - " + activities.size());
        }


        for (Activity activityDestination : activities.subList(1, activities.size())) {
            TransportMode transportMode = null;

            int tripDurationSeconds = 20;


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
            activityOrigin = activityDestination; // update past activity
        }

        if (person.getPid() == 45049) {
            LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + person.getPid());

            // just for printing for each trip in the chain
            for (Trip trip : this.tripChain.getTripChain()) {
                LOGGER.log(Level.INFO,trip.getDepartureActivityType() +"("+ trip.getPreviousActivityTime() + ") -> " + trip.getArrivalActivityType() +"("+ trip.getNextActivityTime() + ") - " + trip.getTransportMode().getStringCode());
            }

        }


        return this.tripChain;
    }
}
