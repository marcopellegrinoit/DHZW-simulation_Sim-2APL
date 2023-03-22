package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.LocationEntry;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.util.Map;
import java.util.logging.Logger;

public class Activity {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private final long pid;
    private final long hid;
    private final int activityNumber;
    private final ActivityType activityType;
    private final ActivityTime startTime;
    private final ActivityTime endTime;

    private final int duration;
    private LocationEntry location;

    public Activity(long pid, long hid, int activityNumber, ActivityType activityType, ActivityTime startTime, int duration) {
        this.pid = pid;
        this.hid = hid;
        this.activityNumber = activityNumber;
        this.activityType = activityType;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = new ActivityTime(startTime.getSeconds()+duration);
    }

    public static Activity fromLine(Map<String, String> keyValue) {
        return new Activity(
                ParserUtil.parseAsLong(keyValue.get("pid")),
                ParserUtil.parseAsLong(keyValue.get("hh_ID")),
                ParserUtil.parseAsInt(keyValue.get("activity_number")),
                StringCodeTypeInterface.parseAsEnum(ActivityType.class, keyValue.get("activity_type")),
                new ActivityTime(ParserUtil.parseAsInt(keyValue.get("start_time_seconds"))),
                ParserUtil.parseAsInt(keyValue.get("duration_seconds"))
                );
    }

    public String toString() {
        // start_time, duration, location, mask_state, disease_state

        return String.format(
                "%s (%s) %s - %s",
                this.getActivityType(),
                this.startTime,
                new ActivityTime(this.startTime.getSeconds())
        );
    }

    public long getPid() {
        return this.pid;
    }
    public long getHid() {
        return this.hid;
    }
    public int getActivityNumber() {
        return this.activityNumber;
    }
    public ActivityType getActivityType() {
        return this.activityType;
    }

    public ActivityTime getStartTime() {
        return startTime;
    }

    public LocationEntry getLocation() {
        return location;
    }
    public void setLocation(LocationEntry location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public ActivityTime getEndTime() {
        return endTime;
    }

    public int getIntegerStartTime (){
        return this.getStartTime().getSeconds();
    }
}
