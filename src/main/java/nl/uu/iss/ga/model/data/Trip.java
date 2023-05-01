package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.logging.Level;

public class Trip {
    private boolean personDriver;
    private boolean personPassenger;
    private final long pid;
    private final long hid;
    private final Activity departureActivity;
    private final Activity arrivalActivity;
    private final double beelineDistance;
    private final int activityTimeGap;
    private TransportMode transportMode;

    public Trip(long pid, long hid, Activity departureActivity, Activity arrivalActivity, TransportMode transportMode, double beelineDistance) {
        this.pid = pid;
        this.hid = hid;
        this.departureActivity = departureActivity;
        this.arrivalActivity = arrivalActivity;
        this.activityTimeGap = departureActivity.getEndTime().getSeconds() - arrivalActivity.getStartTime().getSeconds();
        this.beelineDistance = beelineDistance;
        this.transportMode = transportMode;
    }

    @Override
    public String toString() {
        return "[" +
                this.departureActivity.getActivityType() +
                ", " + this.departureActivity.getLocation().getPostcode() +
                ", " +  this.departureActivity.getStartTime() +
                " - " + this.departureActivity.getEndTime() +
                "] --> [" +
                this.arrivalActivity.getActivityType() +
                ", " + this.arrivalActivity.getLocation().getPostcode() +
                ", " + this.arrivalActivity.getStartTime() +
                "] - " + this.transportMode +
                "";
    }

    // region getter and setter

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
    public Activity getDepartureActivity() {
        return this.departureActivity;
    }
    public Activity getArrivalActivity() {
        return this.arrivalActivity;
    }
    public int getActivityTimeGap() {
        return activityTimeGap;
    }
    public double getBeelineDistance(){return this.beelineDistance;}

    public TransportMode getTransportMode(){return this.transportMode;}
    public void setTransportMode(TransportMode transportMode){this.transportMode = transportMode;}

    // endregion getter and setter
}
