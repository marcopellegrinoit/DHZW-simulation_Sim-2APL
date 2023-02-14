package main.java.nl.uu.iss.ga.simulation.agent.planscheme;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.plan.activity.ExecuteTourPlan;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

import java.util.logging.Logger;

public class GoalPlanScheme implements PlanScheme<TripTour> {

    private static final Logger LOGGER = Logger.getLogger(GoalPlanScheme.class.getName());

    AgentContextInterface<TripTour> agentContextInterface;

    @Override
    public Plan<TripTour> instantiate(Trigger trigger, AgentContextInterface<TripTour> agentContextInterface) {
        this.agentContextInterface = agentContextInterface;
        BeliefContext context = agentContextInterface.getContext(BeliefContext.class);

        // for each activity in the weekly activity schedule
        if (trigger instanceof ActivityTour) {

            ActivityTour activityTour = (ActivityTour) trigger;

            // look for a plan only if the chain is about this new day
            if (context.getToday().equals(activityTour.getDay())) {
                return new ExecuteTourPlan(activityTour);
            }
        }

        return Plan.UNINSTANTIATED();
    }
}
