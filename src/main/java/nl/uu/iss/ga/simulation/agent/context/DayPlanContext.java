package main.java.nl.uu.iss.ga.simulation.agent.context;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.TripActivity;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DayPlanContext implements Context {

    private static final Logger LOGGER = Logger.getLogger(DayPlanContext.class.getName());

    private DayOfWeek dayOfWeek;
    private List<Activity> daySchedule;
    private boolean adjustTime = false;

    private TripActivity lastTripActivity;

    public boolean testIsDayOfWeek(DayOfWeek dayOfWeek) {
        return this.dayOfWeek != null && this.dayOfWeek.equals(dayOfWeek);
    }

    public void resetDaySchedule(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.daySchedule = new ArrayList<>();
        this.lastTripActivity = null;
        this.adjustTime = false;
    }

    public void addCandidateActivity(Activity activity) {
        if(!activity.getStartTime().getDayOfWeek().equals(this.dayOfWeek)) {
            LOGGER.log(Level.WARNING, String.format(
                    "Trying to add activity for %s but today is %s",
                    activity.getStartTime().getDayOfWeek(), this.dayOfWeek));
        }
        for(int i = 0; i < daySchedule.size(); i++) {
            if(activity.getStartTime().getSeconds() < this.daySchedule.get(i).getStartTime().getSeconds()) {
                this.daySchedule.add(i, activity);
                return;
            }
        }
        this.daySchedule.add(activity);
    }

    public Activity getLastActivity() {
        if(this.daySchedule.isEmpty()) {
            return null;
        } else {
            return this.daySchedule.get(this.daySchedule.size() - 1);
        }
    }

    public int getFirstAvailableTime() {
        if(this.daySchedule.isEmpty()) {
            return this.dayOfWeek.getSecondsSinceMidnightForDayStart();
        } else {
            return getLastActivity().getStartTime().getSeconds() +
                    getLastActivity().getDuration();
        }
    }

    public TripActivity getLastTripActivity() {
        return lastTripActivity;
    }

    public void setLastTripActivity(TripActivity lastTripActivity) {
        this.lastTripActivity = lastTripActivity;
    }

    public boolean isAdjustTime() {
        return adjustTime;
    }

    public void setAdjustTime(boolean adjustTime) {
        this.adjustTime = adjustTime;
    }
}
