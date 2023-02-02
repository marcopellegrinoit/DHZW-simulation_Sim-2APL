package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TripChain extends Goal implements Cloneable {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private List<Trip> chain;
    private DayOfWeek day;
    private long pid;

    public TripChain(long pid, DayOfWeek day, List<Trip> chain) {
        this.pid = pid;
        this.day = day;
        this.chain = chain;
    }
    public TripChain(long pid, DayOfWeek day) {
        this.pid = pid;
        this.day = day;
        this.chain = new ArrayList<Trip>();
    }

    @Override
    public boolean isAchieved(AgentContextInterface agentContextInterface) {
        // Activity should never be associated with a plan as a goal, and should never be achieved
        return false;
    }

    @Override
    public String toString() {
        // start_time, duration, location, mask_state, disease_state

        return String.format(
                "%s (%s) %s - %s",
                this.chain
        );
    }

    @Override
    public TripChain clone() {
        TripChain tripChain = new TripChain(this.pid, this.day, this.chain);
        return tripChain;
    }

    public void setChain(List<Trip> chain) {
        this.chain = chain;
    }

    public void addTrip(Trip trip) {
        this.chain.add(trip);
    }

    public List<Trip> getTripChain() {return this.chain;}
    public void setDay(DayOfWeek day){
        this.day = day;
    }
    public void setPid(long pid){
        this.pid = pid;
    }
    public DayOfWeek getDay() {return this.day;}
    public long getPid() {return this.pid;}
}
