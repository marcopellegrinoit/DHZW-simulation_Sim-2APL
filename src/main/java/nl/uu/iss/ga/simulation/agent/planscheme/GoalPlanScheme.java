package main.java.nl.uu.iss.ga.simulation.agent.planscheme;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.plan.activity.ExecuteChainTripPlan;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoalPlanScheme implements PlanScheme<TripChain> {

    private static final Logger LOGGER = Logger.getLogger(GoalPlanScheme.class.getName());

    AgentContextInterface<TripChain> agentContextInterface;

    @Override
    public Plan<TripChain> instantiate(Trigger trigger, AgentContextInterface<TripChain> agentContextInterface) {
        this.agentContextInterface = agentContextInterface;
        BeliefContext context = agentContextInterface.getContext(BeliefContext.class);

        // for each activity in the weekly activity schedule
        if (trigger instanceof ActivityChain) {
            ActivityChain activityChain = (ActivityChain) trigger;

            // look for a plan only if the chain is about this new day
            if (context.getToday().equals(activityChain.getDay())) {

                return new ExecuteChainTripPlan(activityChain);

/*                if (activityChain.getPid() == 84) {
                    LOGGER.log(Level.INFO,"GoalPlanScheme - " + activityChain.getDay() + " - " + activityChain.getActivityChain().size());

                    List <Activity> activities = activityChain.getActivityChain();

                    // Instantiate new empty chain of trips
                    TripChain tripChain = new TripChain(activityChain.getPid(), activityChain.getDay());

                    Activity activityOrigin = activities.get(0);
                    for (Activity activityDestination : activities.subList(1, activities.size())) {
                        tripChain.addTrip(new Trip(activityOrigin.getPid(),
                                                    activityOrigin.getHid(),
                                                    activityDestination.getActivityType(),
                                                    activityOrigin.getLocation().getPc4(),
                                                    activityDestination.getLocation().getPc4(),
                                                    activityOrigin.getStartTime(),
                                                    activityDestination.getStartTime()
                        ));
                        activityOrigin = activityDestination; // update past activity
                    }

                    return new ExecuteChainTripPlan(tripChain);
                }*/

            }

        }


        return Plan.UNINSTANTIATED();
    }
}
