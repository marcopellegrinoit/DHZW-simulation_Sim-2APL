package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.Trip;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DayPlanContext implements Context {

    private static final Logger LOGGER = Logger.getLogger(DayPlanContext.class.getName());

    private DayOfWeek dayOfWeek;
    private List<Trip> daySchedule;

    private Trip lastTrip;

    public boolean testIsDayOfWeek(DayOfWeek dayOfWeek) {
        return this.dayOfWeek != null && this.dayOfWeek.equals(dayOfWeek);
    }

    public void resetDaySchedule(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.daySchedule = new ArrayList<>();
        this.lastTrip = null;
    }

    public void addTrip(Trip trip) {
        if(!trip.getDepartureTime().getDayOfWeek().equals(this.dayOfWeek)) {
            LOGGER.log(Level.WARNING, String.format(
                    "Trying to add activity for %s but today is %s",
                    trip.getDepartureTime().getDayOfWeek(), this.dayOfWeek));
        }
        // loop through the activity schedule and place the activity at the right place
        for(int i = 0; i < daySchedule.size(); i++) {
            if(trip.getDepartureTime().getSeconds() < this.daySchedule.get(i).getDepartureTime().getSeconds()) {
                this.daySchedule.add(i, trip);
                return;
            }
        }
        // if it is the last activity of the day
        this.daySchedule.add(trip);
    }

    public Trip getLastTrip() {
        if(this.daySchedule.isEmpty()) {
            return null;
        } else {
            return this.daySchedule.get(this.daySchedule.size() - 1);
        }
    }

    public Trip getLastTripActivity() {
        return lastTrip;
    }

    public void setLastTrip(Trip lastTripActivity) {
        this.lastTrip = lastTrip;
    }

}
