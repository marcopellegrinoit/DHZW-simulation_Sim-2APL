package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

public class ActivitySchedule implements Context {
    private final long hid;
    private final long pid;
    private final SortedMap<ActivityTime, Activity> schedule;

    public ActivitySchedule(long hid, long pid, SortedMap<ActivityTime, Activity> schedule) {
        this.hid = hid;
        this.pid = pid;
        this.schedule = schedule;
    }

    public long getHid() {
        return hid;
    }

    public long getPid() {
        return pid;
    }

    public SortedMap<ActivityTime, Activity> getSchedule() {
        return schedule;
    }

    /**
     * Return the first activity that is not a trip that is planned after the given time
     **/
    public Activity getScheduledActivityAfter(DayOfWeek dayOfWeek, int secondsOfDay) {
        return getScheduledActivityAfter(dayOfWeek.getSecondsSinceMidnightForDayStart() + secondsOfDay);
    }

    /**
     * Return the first activity that is not a trip that is planned after the given time
     */
    public Activity getScheduledActivityAfter(int secondsSinceSundayMidnight) {
        for(ActivityTime time : this.schedule.keySet()) {
            if(time.getSeconds() > secondsSinceSundayMidnight) {
                return this.schedule.get(time);
            }
        }
        return null;
    }

}
