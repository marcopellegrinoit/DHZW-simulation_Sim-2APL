package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

public class Trip {
    private final TransportMode transportMode;
    private boolean personDriver;
    private boolean personPassenger;
    private final long pid;
    private final long hid;
    private final ActivityType arrivalActivityType;
    private final String departurePC4;
    private final String arrivalPC4;
    private final ActivityTime departureTime;
    private final ActivityTime arrivalTime;
    private final ActivityTime previousActivityTime;
    private final ActivityTime nextActivityTime;


    public Trip(long pid, long hid, ActivityType arrivalActivityType, String departurePC4, String arrivalPC4, ActivityTime previousActivityTime, ActivityTime nextActivityTime, ActivityTime departureTime, ActivityTime arrivalTime, TransportMode transportMode) {
        this.pid = pid;
        this.hid = hid;
        this.arrivalActivityType = arrivalActivityType;
        this.departurePC4 = departurePC4;
        this.arrivalPC4 = arrivalPC4;
        this.previousActivityTime = previousActivityTime;
        this.nextActivityTime = nextActivityTime;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.transportMode = transportMode;
    }

    public String toString() {
        return "TripActivity{" +
                "transportMode=" + transportMode +
                '}';
    }

    // region getter and setter

    public ActivityType getArrivalActivityType() {
        return this.arrivalActivityType;
    }

    public ActivityTime getPreviousActivityTime() {
        return this.previousActivityTime;
    }

    public ActivityTime getNextActivityTime() {
        return this.nextActivityTime;
    }

    public ActivityTime getDepartureTime() {
        return this.departureTime;
    }

    public ActivityTime getArrivalTime() {
        return this.arrivalTime;
    }

    public boolean isPersonDriver() {
        return this.personDriver;
    }

    public void setPersonDriver(boolean personDriver) {
        this.personDriver = personDriver;
    }

    public void setPersonPassenger(boolean personPassenger) {
        this.personPassenger = personPassenger;
    }
    public boolean isPersonPassenger() {
        return this.personPassenger;
    }

    public long getPid() {
        return this.pid;
    }

    public long getHid() {
        return this.hid;
    }

    public String getDeparturePC4() {
        return this.departurePC4;
    }

    public String getArrivalPC4() {
        return this.arrivalPC4;
    }

    public TransportMode getTransportMode() { return this.transportMode;}

    // endregion getter and setter
}
