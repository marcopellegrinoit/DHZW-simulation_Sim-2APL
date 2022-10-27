package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.ActivitySchedule;
import main.java.nl.uu.iss.ga.model.data.ActivityTime;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.DayPlanContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CancelActivityPlan extends RunOncePlan<Activity> {
    
    private static final Logger LOGGER = Logger.getLogger(CancelActivityPlan.class.getName());

    // Odds that the activity will be replaced by going home. Otherwise, this
    // activity will just be dropped, and the next activity will be scheduled in its place
    public static final double GO_HOME_IF_CANCELLED_PROBABILITY = .5;

    private Activity activity;

    public CancelActivityPlan(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Activity executeOnce(PlanToAgentInterface<Activity> planToAgentInterface) {
        DayPlanContext context = planToAgentInterface.getContext(DayPlanContext.class);
        BeliefContext beliefContext = planToAgentInterface.getContext(BeliefContext.class);

        if (!context.testIsDayOfWeek(beliefContext.getToday())) {
            context.resetDaySchedule(beliefContext.getToday());
        }

        Activity lastActivity = context.getLastActivity();
        ActivitySchedule schedule = planToAgentInterface.getContext(ActivitySchedule.class);
        Activity nextActivity = schedule.getScheduledActivityAfter(this.activity.getStartTime().getSeconds());

        /* Stay home if one of:
            - Last activity was HOME
            - Next activity is HOME or WORK
            - This would have been the last activity of the day
            - Otherwise just go home in x% of the cases
         */

        if (
                beliefContext.getHomeLocation() != null && beliefContext.getHomeLocation().getLocationID() != null && ( // Avoid null poiinter in 1 case
                    nextActivity == null ||
                    !nextActivity.getStartTime().getDayOfWeek()
                        .equals(this.activity.getStartTime().getDayOfWeek()) ||
                    this.activity.getActivityType().equals(ActivityType.WORK) ||
                    this.activity.getActivityType().equals(ActivityType.HOME) ||
                    (lastActivity != null && lastActivity.getActivityType().equals(ActivityType.HOME)) ||
                    beliefContext.getRandom().nextDouble() < GO_HOME_IF_CANCELLED_PROBABILITY
                )
        ) {

            int starttime = context.getFirstAvailableTime();
            ActivityTime scheduledTime = this.activity.getStartTime();

            if (
                    lastActivity != null &&
                    !lastActivity.getActivityType().equals(ActivityType.HOME) &&
                    context.getLastTripActivity() != null
            ) {
                starttime += context.getLastTripActivity().getDuration();
                context.setLastTripActivity(null);
            }

            this.activity.setStartTime(
                    new ActivityTime(starttime));

            if (nextActivity != null && !nextActivity.getStartTime().getDayOfWeek()
                    .equals(activity.getStartTime().getDayOfWeek()))
            {
                activity.setDuration(activity.getStartTime().getDurationUntilEndOfDay());
            }

            // Sanity check

            if (
                    !activity.getStartTime().getDayOfWeek().equals(scheduledTime.getDayOfWeek())
            ) {
                ActivityTime ends = new ActivityTime(activity.getStartTime().getSeconds() + activity.getDuration());
                if (!(ends.getDayOfWeek() == null && scheduledTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) &&
                        (ends.getDayOfWeek() != null && !ends.getDayOfWeek().equals(scheduledTime.getDayOfWeek()))) {
                    LOGGER.log(Level.WARNING, String.format(
                            "When modifying activity %d of person %d to cancel the activity, the day was changed from %s to %s",
                            activity.getActivityNumber(),
                            activity.getPid(),
                            scheduledTime.getDayOfWeek(),
                            ends.getDayOfWeek()
                    ));
                }
            }

            context.addCandidateActivity(activity);
            return this.activity;
        } else {
            // This means the agent does not perform any activity. The next activity can take the time
            // of the current activity that is cancelled
            context.setAdjustTime(true);
            return null;
        }
    }
}
