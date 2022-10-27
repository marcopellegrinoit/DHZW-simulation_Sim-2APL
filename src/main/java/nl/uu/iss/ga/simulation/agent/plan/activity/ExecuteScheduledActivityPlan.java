package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.ActivitySchedule;
import main.java.nl.uu.iss.ga.model.data.ActivityTime;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.DayPlanContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanExecutionError;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteScheduledActivityPlan extends RunOncePlan<Activity> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteScheduledActivityPlan.class.getName());

    private Activity activity;

    public ExecuteScheduledActivityPlan(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Activity executeOnce(PlanToAgentInterface<Activity> planToAgentInterface) throws PlanExecutionError {
        DayPlanContext context = planToAgentInterface.getContext(DayPlanContext.class);
        BeliefContext beliefContext = planToAgentInterface.getContext(BeliefContext.class);
        ActivitySchedule schedule = planToAgentInterface.getContext(ActivitySchedule.class);

        if (!context.testIsDayOfWeek(beliefContext.getToday())) {
            context.resetDaySchedule(beliefContext.getToday());
        }

        if (context.isAdjustTime()) {
            int newStartTime = context.getFirstAvailableTime();

            if (context.getLastTripActivity() != null) {
                newStartTime += context.getLastTripActivity().getDuration();
                context.setLastTripActivity(null);
            }

            ActivityTime makeStart = new ActivityTime(newStartTime);
            if(!makeStart.getDayOfWeek().equals(activity.getStartTime().getDayOfWeek())) {
                LOGGER.log(Level.INFO, String.format(
                    "Activity was changed from %s to %s",
                        activity.getStartTime().getDayOfWeek(),
                        makeStart.getDayOfWeek()));
            }
            activity.setStartTime(makeStart);
            Activity nextPlannedActivity = schedule.getScheduledActivityAfter(activity.getStartTime().getSeconds());

            if (activity.getActivityType().equals(ActivityType.HOME) &&
                    (
                            nextPlannedActivity == null ||
                                    !nextPlannedActivity.getStartTime().getDayOfWeek()
                                            .equals(activity.getStartTime().getDayOfWeek())
                    )
            ) {
                // Make duration end of day
                activity.setDuration(activity.getStartTime().getDurationUntilEndOfDay());
            } else if (nextPlannedActivity.getActivityType().equals(ActivityType.WORK)) {
                // Stretch current activity, because work won't start earlier.
                activity.setDuration(nextPlannedActivity.getStartTime().getSeconds() -
                        activity.getStartTime().getSeconds());
            }

        }

        // Sanity check
        if (
                !activity.getStartTime().getDayOfWeek().equals(beliefContext.getToday()) ||
                        !new ActivityTime(activity.getStartTime().getSeconds() + activity.getDuration() -1 ).getDayOfWeek().equals(beliefContext.getToday())
        ) {
            LOGGER.log(Level.WARNING, String.format(
                    "Day changed when shifting activity %d for person %d",
                    activity.getActivityNumber(), activity.getPid()));
        }


        context.addCandidateActivity(activity);

        return activity;
    }
}
