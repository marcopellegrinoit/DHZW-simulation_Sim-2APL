package main.java.nl.uu.iss.ga.simulation.agent.planscheme;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.TripActivity;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.plan.AdjustTrustAttitudePlan;
import main.java.nl.uu.iss.ga.simulation.agent.plan.SleepGoal;
import main.java.nl.uu.iss.ga.simulation.agent.plan.SleepPlan;
import main.java.nl.uu.iss.ga.simulation.agent.plan.activity.CancelActivityPlan;
import main.java.nl.uu.iss.ga.simulation.agent.plan.activity.ExecuteScheduledActivityPlan;
import main.java.nl.uu.iss.ga.simulation.agent.plan.activity.HandleTripPlan;
import main.java.nl.uu.iss.ga.simulation.agent.trigger.AdjustTrustAttitudeGoal;
import main.java.nl.uu.iss.ga.util.tracking.activities.InfluencedActivitiesInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

import java.util.logging.Logger;

public class GoalPlanScheme implements PlanScheme<Activity> {

    private static final Logger LOGGER = Logger.getLogger(GoalPlanScheme.class.getName());

    public static InfluencedActivitiesInterface influencedActivitiesTracker;

    AgentContextInterface<Activity> agentContextInterface;

    @Override
    public Plan<Activity> instantiate(Trigger trigger, AgentContextInterface<Activity> agentContextInterface) {
        this.agentContextInterface = agentContextInterface;
        BeliefContext context = agentContextInterface.getContext(BeliefContext.class);

        // for each activity in the weekly schedule
        if (trigger instanceof Activity) {
            Activity activity = (Activity) trigger;

            // check if the time corresponds to today (day-based)
            // remove this block and work here

            if (context.getToday().equals(activity.getStartTime().getDayOfWeek())) {
                // Trigger applies to today

                if (activity.getActivityType().equals(ActivityType.TRIP)) {
                    return new HandleTripPlan((TripActivity) activity);
                } else {
                    return new ExecuteScheduledActivityPlan(activity);
                }

            }
        } else if (trigger instanceof AdjustTrustAttitudeGoal) {
            AdjustTrustAttitudeGoal adjustTrustAttitudeGoal = (AdjustTrustAttitudeGoal) trigger;
            if (context.getCurrentTick() >= adjustTrustAttitudeGoal.getFatigueStart()) {
                return new AdjustTrustAttitudePlan(adjustTrustAttitudeGoal);
            }
        } else if (trigger instanceof SleepGoal) {
            return new SleepPlan((SleepGoal) trigger);
        }

        return Plan.UNINSTANTIATED();
    }

}
