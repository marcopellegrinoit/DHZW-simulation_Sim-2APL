package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.HashMap;

/**
 * Stores agents general beliefs
 */
public class RoutingBeliefContext implements Context {
    private AgentID me;
    private final EnvironmentInterface environmentInterface;

    private HashMap<TwoStringKeys, Double> walkTimes;
    private HashMap<TwoStringKeys, Double> bikeTimes;
    private HashMap<TwoStringKeys, Double> carTimes;
    private HashMap<TwoStringKeys, Double> walkDistances;
    private HashMap<TwoStringKeys, Double> bikeDistances;
    private HashMap<TwoStringKeys, Double> carDistances;

    public RoutingBeliefContext(EnvironmentInterface environmentInterface) {
        this.environmentInterface = environmentInterface;
        this.walkTimes = new HashMap<TwoStringKeys, Double>();
        this.walkDistances = new HashMap<TwoStringKeys, Double>();
        this.bikeTimes = new HashMap<TwoStringKeys, Double>();
        this.bikeDistances = new HashMap<TwoStringKeys, Double>();
        this.carTimes = new HashMap<TwoStringKeys, Double>();
        this.carDistances = new HashMap<TwoStringKeys, Double>();
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

    public HashMap<TwoStringKeys, Double> getWalkTimes() {
        return walkTimes;
    }
    public HashMap<TwoStringKeys, Double> getBikeTimes() {
        return bikeTimes;
    }

    public HashMap<TwoStringKeys, Double> getCarTimes() {
        return carTimes;
    }

    public HashMap<TwoStringKeys, Double> getWalkDistances() {
        return walkDistances;
    }

    public HashMap<TwoStringKeys, Double> getBikeDistances() {
        return bikeDistances;
    }

    public HashMap<TwoStringKeys, Double> getCarDistances() {
        return carDistances;
    }

    public void addWalkTime(TwoStringKeys key, double time) {
        if(!walkTimes.containsKey(key)) {
            this.walkTimes.put(key, time);
        }
    }
    public void addBikeTime(TwoStringKeys key, double time) {
        if(!bikeTimes.containsKey(key)) {
            this.bikeTimes.put(key, time);
        }
    }
    public void addCarTime(TwoStringKeys key, double time) {
        if(!carTimes.containsKey(key)) {
            this.carTimes.put(key, time);
        }
    }

    public void addWalkDistance(TwoStringKeys key, double time) {
        if(!walkDistances.containsKey(key)) {
            this.walkDistances.put(key, time);
        }
    }
    public void addBikeDistance(TwoStringKeys key, double time) {
        if(!bikeDistances.containsKey(key)) {
            this.bikeDistances.put(key, time);
        }
    }
    public void addCarDistance(TwoStringKeys key, double time) {
        if(!carDistances.containsKey(key)) {
            this.carDistances.put(key, time);
        }
    }
}
