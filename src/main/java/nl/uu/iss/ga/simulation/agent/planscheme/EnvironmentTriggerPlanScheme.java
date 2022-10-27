package main.java.nl.uu.iss.ga.simulation.agent.planscheme;

import main.java.nl.uu.iss.ga.model.data.Activity;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

public class EnvironmentTriggerPlanScheme implements PlanScheme<Activity> {

    @Override
    public Plan<Activity> instantiate(Trigger trigger, AgentContextInterface<Activity> agentContextInterface) {
        return Plan.UNINSTANTIATED();
    }
}
