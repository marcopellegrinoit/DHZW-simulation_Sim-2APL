package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class TripTour extends Goal implements Cloneable {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private List<Trip> chain;
    private final DayOfWeek day;
    private final long pid;

    public TripTour(long pid, DayOfWeek day, List<Trip> chain) {
        this.pid = pid;
        this.day = day;
        this.chain = chain;
    }
    public TripTour(long pid, DayOfWeek day) {
        this.pid = pid;
        this.day = day;
        this.chain = new ArrayList<Trip>();
    }

    public void sortTripsByDistance() {
        // Use a lambda expression to define a custom Comparator that compares Trips based on distance
        Comparator<Trip> compareByDistance = (Trip t1, Trip t2) -> Double.compare(t2.getBeelineDistance(), t1.getBeelineDistance());
        // Sort the tripList using the custom Comparator
        this.chain.sort(compareByDistance);
    }

    @Override
    public boolean isAchieved(AgentContextInterface agentContextInterface) {
        // Activity should never be associated with a plan as a goal, and should never be achieved
        return false;
    }

    @Override
    public String toString() {
        String output = "TripTour [pid: "+ pid + " (n trips:"+chain.size()+") {\n";
        if (this.chain.size()>0) {
            for (Trip trip: this.chain){
                output = output + trip.toString() + "\n";
            }
        }

        return output;
    }

    @Override
    public TripTour clone() {
        TripTour tripTour = new TripTour(this.pid, this.day, this.chain);
        return tripTour;
    }

    public void addTrip(Trip trip) {
        this.chain.add(trip);
    }

    public List<Trip> getTripChain() {return this.chain;}
    public DayOfWeek getDay() {return this.day;}
    public long getPid() {return this.pid;}

}
