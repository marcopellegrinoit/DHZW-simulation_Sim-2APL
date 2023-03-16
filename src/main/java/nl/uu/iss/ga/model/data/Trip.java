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
    private final String departurePostcode;
    private final String arrivalPostcode;
    private final ActivityTime previousActivityStartTime;

    private final ActivityTime previousActivityEndTime;
    private final ActivityTime nextActivityStartTime;
    private final int activityTimeGap;
    private TransportMode transportMode;
    private double travelTime;
    private double travelDistance;

    public Trip(long pid, long hid, ActivityType departureActivityType, ActivityType arrivalActivityType, String departurePostcode, String arrivalPostcode, ActivityTime previousActivityStartTime, ActivityTime previousActivityEndTime, ActivityTime nextActivityStartTime, TransportMode transportMode, double travelTime, double travelDistance) {
        this.pid = pid;
        this.hid = hid;
        this.departureActivityType = departureActivityType;
        this.arrivalActivityType = arrivalActivityType;
        this.departurePostcode = departurePostcode;
        this.arrivalPostcode = arrivalPostcode;
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
                "origin_Activity = " + this.departureActivityType +
                ", postcode = " + this.departurePostcode +
                " (" + this.previousActivityStartTime +
                " - " + this.previousActivityEndTime +
                ") --> " +
                "origin_Activity = " + this.arrivalActivityType +
                ", postcode = " + this.arrivalPostcode +
                " (starts at " + this.nextActivityStartTime +
                "). [mode:" + this.transportMode +
                " - time: " + this.travelTime + " mins" +
                " - distance: " + this.travelDistance + " km" +
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

    public String getDeparturePostcode() {
        return this.departurePostcode;
    }

    public String getArrivalPostcode() {
        return this.arrivalPostcode;
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

    public double getTravelTime(){return this.travelTime;}

    public double getTravelDistance(){return this.travelDistance;}

    public TransportMode getTransportMode(){return this.transportMode;}
    public void setTravelTime(int travelTime){this.travelTime = travelTime;}

    public void setTravelDistance(int travelDistance){this.travelDistance = travelDistance;}

    public void setTransportMode(TransportMode transportMode){this.transportMode = transportMode;}

    // endregion getter and setter
}
