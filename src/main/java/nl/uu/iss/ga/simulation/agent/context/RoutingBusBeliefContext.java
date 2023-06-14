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
public class RoutingBusBeliefContext implements Context {
    private AgentID me;
    private final EnvironmentInterface environmentInterface;
    private HashMap<String, HashMap<String, Double>> busTimes;
    private HashMap<String, HashMap<String, Double>> busDistances;
    private HashMap<String, HashMap<String, Double>> walkTimes;
    private HashMap<String, HashMap<String, Integer>> nChanges;
    private HashMap<String, HashMap<String, String>> postcodeStops;
    private HashMap<String, HashMap<String, Integer>> feasibleFlags;
    private HashMap<String, HashMap<String, Double>> totalDistances;


    public RoutingBusBeliefContext(EnvironmentInterface environmentInterface) {
        this.environmentInterface = environmentInterface;
        this.busTimes = new HashMap<String, HashMap<String, Double>>();
        this.busDistances = new HashMap<String, HashMap<String, Double>>();
        this.walkTimes = new HashMap<String, HashMap<String, Double>>();
        this.nChanges = new HashMap<String, HashMap<String, Integer>>();
        this.postcodeStops = new HashMap<String, HashMap<String, String>>();
        this.feasibleFlags = new HashMap<String, HashMap<String, Integer>>();
        this.totalDistances = new HashMap<String, HashMap<String, Double>>();
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
    // getter

    public double getBusTime(String departure, String arrival){
        return this.busTimes.get(departure).get(arrival);
    }
    public double getBusDistance(String departure, String arrival){
        return this.busDistances.get(departure).get(arrival);
    }
    public double getWalkTime(String departure, String arrival){
        return this.walkTimes.get(departure).get(arrival);
    }
    public int getChange(String departure, String arrival){
        return this.nChanges.get(departure).get(arrival);
    }
    public String getPostcodeDHZW(String departure, String arrival){
        return this.postcodeStops.get(departure).get(arrival);
    }
    public int getFeasibleFlag(String departure, String arrival){
        return this.feasibleFlags.get(departure).get(arrival);
    }
    public double getTotalDistance(String departure, String arrival){
        return this.totalDistances.get(departure).get(arrival);
    }



    //******************************************************************************************************************
    // setter

    public void addBusTime(String location1, String location2, double transitTime) {
        if(this.busTimes.containsKey(location1)) {
            this.busTimes.get(location1).put(location2, transitTime);
        } else {
            HashMap<String, Double> tmpMap = new HashMap<String, Double>();
            tmpMap.put(location2, transitTime);
            this.busTimes.put(location1, tmpMap);
        }
    }
    public void addBusDistance(String location1, String location2, double transitDistance) {
        if(this.busDistances.containsKey(location1)) {
            this.busDistances.get(location1).put(location2, transitDistance);
        } else {
            HashMap<String, Double> tmpMap = new HashMap<String, Double>();
            tmpMap.put(location2, transitDistance);
            this.busDistances.put(location1, tmpMap);
        }
    }
    public void addWalkTime(String location1, String location2, double walkTime) {
        if(this.walkTimes.containsKey(location1)) {
            this.walkTimes.get(location1).put(location2, walkTime);
        } else {
            HashMap<String, Double> tmpMap = new HashMap<String, Double>();
            tmpMap.put(location2, walkTime);
            this.walkTimes.put(location1, tmpMap);
        }
    }
    public void addChanges(String location1, String location2, int nChanges) {
        if(this.nChanges.containsKey(location1)) {
            this.nChanges.get(location1).put(location2, nChanges);
        } else {
            HashMap<String, Integer> tmpMap = new HashMap<String, Integer>();
            tmpMap.put(location2, nChanges);
            this.nChanges.put(location1, tmpMap);
        }
    }
    public void addPostcodeStop(String location1, String location2, String postcode) {
        if(this.postcodeStops.containsKey(location1)) {
            this.postcodeStops.get(location1).put(location2, postcode);
        } else {
            HashMap<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put(location2, postcode);
            this.postcodeStops.put(location1, tmpMap);
        }
    }

    public void addFeasibleFlag(String location1, String location2, int feasibleFlag) {
        if(this.feasibleFlags.containsKey(location1)) {
            this.feasibleFlags.get(location1).put(location2, feasibleFlag);
        } else {
            HashMap<String, Integer> tmpMap = new HashMap<String, Integer>();
            tmpMap.put(location2, feasibleFlag);
            this.feasibleFlags.put(location1, tmpMap);
        }
    }

    public void addTotalDistance(String location1, String location2, double totalDistance) {
        if(this.totalDistances.containsKey(location1)) {
            this.totalDistances.get(location1).put(location2, totalDistance);
        } else {
            HashMap<String, Double> tmpMap = new HashMap<String, Double>();
            tmpMap.put(location2, totalDistance);
            this.totalDistances.put(location1, tmpMap);
        }
    }
}
