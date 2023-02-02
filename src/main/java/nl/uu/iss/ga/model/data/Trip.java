package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

public class Trip {
    private TransportMode transportMode;
    private boolean personWasDriver;
    private boolean personWasPassenger;
    private final long pid;
    private final long hid;
    private final ActivityType arrivalActivityType;
    private final String departurePC4;
    private final String arrivalPC4;

    private ActivityTime departureTime;

    private ActivityTime arrivalTime;
    private final ActivityTime previousActivityTime;
    private final ActivityTime nextActivityTime;



    public Trip(long pid, long hid, ActivityType arrivalActivityType, String departurePC4, String arrivalPC4, ActivityTime previousActivityTime, ActivityTime nextActivityTime) {
        this.pid = pid;
        this.hid = hid;
        this.arrivalActivityType = arrivalActivityType;
        this.departurePC4 = departurePC4;
        this.arrivalPC4 = arrivalPC4;
        this.previousActivityTime = previousActivityTime;
        this.nextActivityTime = nextActivityTime;
    }

    public boolean isPersonWasPassenger() {
        return this.personWasPassenger;
    }


    public String toString() {
        return "TripActivity{" +
                "transportMode=" + transportMode +
                ", personWasDriver=" + personWasDriver +
                ", personWasPassenger=" + personWasPassenger +
                '}';
    }

    public ActivityType getArrivalActivityType() {
        return arrivalActivityType;
    }


    public ActivityTime getPreviousActivityTime() {
        return previousActivityTime;
    }

    public ActivityTime getNextActivityTime() {
        return nextActivityTime;
    }

    public ActivityTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ActivityTime departureTime) {
        this.departureTime = departureTime;
    }

    public ActivityTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ActivityTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
