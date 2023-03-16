package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

/**
 * Stores agents general beliefs
 */
public class RoutingBeliefContext implements Context {
    private AgentID me;
    private final EnvironmentInterface environmentInterface;

    public RoutingBeliefContext(
            EnvironmentInterface environmentInterface
    ) {
        this.environmentInterface = environmentInterface;
    }

    public void setAgentID(AgentID me) {
        this.me = me;
    }

    public DayOfWeek getToday() {
        return this.environmentInterface.getToday();
    }

    public long getCurrentTick() {
        return this.environmentInterface.getCurrentTick();
    }

}
