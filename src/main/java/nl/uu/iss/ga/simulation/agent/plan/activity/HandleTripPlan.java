package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.TripActivity;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.DayPlanContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanExecutionError;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

public class HandleTripPlan extends RunOncePlan<Activity> {

    private final TripActivity trip;

    public HandleTripPlan(TripActivity trip) {
        this.trip = trip;
    }

    @Override
    public Activity executeOnce(PlanToAgentInterface<Activity> planInterface) throws PlanExecutionError {
        DayPlanContext context = planInterface.getContext(DayPlanContext.class);
        context.setLastTripActivity(this.trip);
        planInterface.getContext(BeliefContext.class).getModeOfTransportTracker().notifyTransportModeUsed(planInterface.getAgentID(), this.trip.getMode());
        return this.trip;
    }
}
