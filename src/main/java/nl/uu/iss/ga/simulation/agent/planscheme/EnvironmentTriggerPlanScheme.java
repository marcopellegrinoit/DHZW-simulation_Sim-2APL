package main.java.nl.uu.iss.ga.simulation.agent.planscheme;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.ActivityChain;
import main.java.nl.uu.iss.ga.model.data.TripChain;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

import java.util.List;

public class EnvironmentTriggerPlanScheme implements PlanScheme<TripChain> {

    @Override
    public Plan<TripChain> instantiate(Trigger trigger, AgentContextInterface<TripChain> agentContextInterface) {
        return Plan.UNINSTANTIATED();
    }
}
