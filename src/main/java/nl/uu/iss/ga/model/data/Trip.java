package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.logging.Level;

public class Trip {
    private boolean personDriver;
    private boolean personPassenger;
    private final long pid;
    private final long hid;
    private final ActivityType departureActivityType;
    private final ActivityType arrivalActivityType;
    private final String departurePC4;
    private final String arrivalPC4;
    private final String departureLid;
    private final String arrivalLid;
    private final ActivityTime previousActivityStartTime;

    private final ActivityTime previousActivityEndTime;
    private final ActivityTime nextActivityStartTime;
    private final int activityTimeGap;
    private TransportMode transportMode;
    private int travelTime;
    private int travelDistance;

    public Trip(long pid, long hid, ActivityType departureActivityType, ActivityType arrivalActivityType, String departurePC4, String arrivalPC4, String departureLid, String arrivalLid, ActivityTime previousActivityStartTime, ActivityTime previousActivityEndTime, ActivityTime nextActivityStartTime, TransportMode transportMode, int travelTime, int travelDistance) {
        this.pid = pid;
        this.hid = hid;
        this.departureActivityType = departureActivityType;
        this.arrivalActivityType = arrivalActivityType;
        this.departurePC4 = departurePC4;
        this.arrivalPC4 = arrivalPC4;
        this.departureLid = departureLid;
        this.arrivalLid = arrivalLid;
        this.previousActivityStartTime = previousActivityStartTime;
        this.previousActivityEndTime = previousActivityEndTime;
        this.nextActivityStartTime = nextActivityStartTime;
        this.activityTimeGap = nextActivityStartTime.getSeconds() - previousActivityEndTime.getSeconds();
        this.transportMode = transportMode;
        this.travelTime = travelTime;
        this.travelDistance = travelDistance;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "origin_Activity = " + departureActivityType +
                ", origin_id = " +departureLid +
                " (" + previousActivityStartTime +
                " - " + previousActivityEndTime +
                ") --> " +
                "origin_Activity = " + arrivalActivityType +
                ", origin_id = " + arrivalLid +
                " (starts at " + nextActivityStartTime +
                "). [mode:" + transportMode +
                " - time: " + travelTime +
                " - distance: " + travelDistance +
                "]}";
    }


    // region getter and setter

    public ActivityType getDepartureActivityType() {
        return this.departureActivityType;
    }

    public ActivityType getArrivalActivityType() {
        return this.arrivalActivityType;
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

    public String getDepartureLid() {
        return departureLid;
    }

    public String getArrivalLid() {
        return arrivalLid;
    }

    public ActivityTime getPreviousActivityEndTime() {
        return previousActivityEndTime;
    }

    public ActivityTime getNextActivityStartTime() {
        return nextActivityStartTime;
    }

    public int getActivityTimeGap() {
        return activityTimeGap;
    }

    public int getTravelTime(){return this.travelTime;}

    public int getTravelDistance(){return this.travelDistance;}

    public TransportMode getTransportMode(){return this.transportMode;}
    public void setTravelTime(int travelTime){this.travelTime = travelTime;}

    public void setTravelDistance(int travelDistance){this.travelDistance = travelDistance;}

    public void setTransportMode(TransportMode transportMode){this.transportMode = transportMode;}

    // endregion getter and setter
}
