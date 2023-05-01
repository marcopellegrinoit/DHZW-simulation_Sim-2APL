package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.HashMap;

/**
 * Stores agents general beliefs
 */
public class RoutingSimmetricBeliefContext implements Context {
    private AgentID me;
    private final EnvironmentInterface environmentInterface;
    private HashMap<TwoStringKeys, Double> walkTimes;
    private HashMap<TwoStringKeys, Double> bikeTimes;
    private HashMap<TwoStringKeys, Double> carTimes;
    private HashMap<TwoStringKeys, Double> walkDistances;
    private HashMap<TwoStringKeys, Double> bikeDistances;
    private HashMap<TwoStringKeys, Double> carDistances;
    private HashMap<TwoStringKeys, Double> beelineDistances;

    public RoutingSimmetricBeliefContext(EnvironmentInterface environmentInterface) {
        this.environmentInterface = environmentInterface;
        this.walkTimes = new HashMap<TwoStringKeys, Double>();
        this.walkDistances = new HashMap<TwoStringKeys, Double>();
        this.bikeTimes = new HashMap<TwoStringKeys, Double>();
        this.bikeDistances = new HashMap<TwoStringKeys, Double>();
        this.carTimes = new HashMap<TwoStringKeys, Double>();
        this.carDistances = new HashMap<TwoStringKeys, Double>();
        this.beelineDistances = new HashMap<TwoStringKeys, Double>();
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

    //******************************************************************************************************************
    public double getWalkTime(TwoStringKeys key){
        return this.walkTimes.get(key);
    }
    public double getWalkDistance(TwoStringKeys key){
        return this.walkDistances.get(key);
    }
    public double getBikeTime(TwoStringKeys key){
        return this.bikeTimes.get(key);
    }
    public double getBikeDistance(TwoStringKeys key){
        return this.bikeDistances.get(key);
    }
    public double getCarTime(TwoStringKeys key){
        return this.carTimes.get(key);
    }
    public double getCarDistance(TwoStringKeys key){
        return this.carDistances.get(key);
    }
    public double getBeelineDistance(TwoStringKeys key){
        return this.beelineDistances.get(key);
    }

    //******************************************************************************************************************


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

    public void addWalkDistance(TwoStringKeys key, double distance) {
        if(!walkDistances.containsKey(key)) {
            this.walkDistances.put(key, distance);
        }
    }
    public void addBikeDistance(TwoStringKeys key, double distance) {
        if(!bikeDistances.containsKey(key)) {
            this.bikeDistances.put(key, distance);
        }
    }
    public void addCarDistance(TwoStringKeys key, double distance) {
        if(!carDistances.containsKey(key)) {
            this.carDistances.put(key, distance);
        }
    }
    public void addBeelineDistance(TwoStringKeys key, double distance) {
        if(!beelineDistances.containsKey(key)) {
            this.beelineDistances.put(key, distance);
        }
    }

}
