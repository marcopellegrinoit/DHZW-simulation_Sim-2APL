package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.LocationEntry;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.Random;

/**
 * Stores agents general beliefs
 */
public class BeliefContext implements Context {
    private AgentID me;
    private final EnvironmentInterface environmentInterface;
    private double priorTrustAttitude;
    private final LocationEntry homeLocation;

    private final ModeOfTransportTracker modeOfTransportTracker;
    private final ActivityTypeTracker activityTypeTracker;

    public BeliefContext(
            EnvironmentInterface environmentInterface,
            LocationEntry homeLocation,
            ModeOfTransportTracker modeOfTransportTracker,
            ActivityTypeTracker activityTypeTracker
    ) {
        this.environmentInterface = environmentInterface;
        this.homeLocation = homeLocation;
        this.modeOfTransportTracker = modeOfTransportTracker;
        this.activityTypeTracker = activityTypeTracker;
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

    public Random getRandom() {
        return this.environmentInterface.getRnd(this.me);
    }

    public double getPriorTrustAttitude() {
        return priorTrustAttitude;
    }

    public void setPriorTrustAttitude(double priorTrustAttitude) {
        this.priorTrustAttitude = priorTrustAttitude;
    }

    public LocationEntry getHomeLocation() {
        return homeLocation;
    }


    public ModeOfTransportTracker getModeOfTransportTracker() {
        return modeOfTransportTracker;
    }

    public ActivityTypeTracker getActivityTypeTracker() {
        return activityTypeTracker;
    }
}
