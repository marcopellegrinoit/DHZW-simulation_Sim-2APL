package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.logging.Level;

public class Trip {
    private final long pid;
    private final long hid;
    private final Activity departureActivity;
    private final Activity arrivalActivity;
    private final double beelineDistance;
    private final int activityTimeGap;
    private TransportMode transportMode;
    private double distance;

    public Trip(long pid, long hid, Activity departureActivity, Activity arrivalActivity, double beelineDistance) {
        this.pid = pid;
        this.hid = hid;
        this.departureActivity = departureActivity;
        this.arrivalActivity = arrivalActivity;
        this.activityTimeGap = departureActivity.getEndTime().getSeconds() - arrivalActivity.getStartTime().getSeconds();
        this.beelineDistance = beelineDistance;
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
        if (transportMode == null) {
            return false;
        } else {
            return transportMode.equals(TransportMode.CAR_DRIVER);
        }
    }
    public boolean isPersonPassenger() {
        if (transportMode == null) {
            return false;
        } else {
            return transportMode.equals(TransportMode.CAR_PASSENGER);
        }
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

    public void setDistance (double distance) {
        this.distance = distance;
    }
    public double getDistance (){
        return this.distance;
    }

    // endregion getter and setter
}
