package main.java.nl.uu.iss.ga.util.tracking;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This tracker is no longer used. It was developed to debug if the simulation was correctly running the total number of activity types.
 */
public class ActivityTypeTracker {

    private Map<ActivityType, AtomicInteger> activityTypeTrackerMap;

    public void reset() {
        activityTypeTrackerMap = new ConcurrentHashMap<>();
        for(ActivityType type : ActivityType.values()) {
            activityTypeTrackerMap.put(type, new AtomicInteger());
        }
    }

    public void notifyActivityType(ActivityType type) {
        activityTypeTrackerMap.get(type).getAndIncrement();
    }

    public Map<ActivityType, AtomicInteger> getActivityTypeTrackerMap() {
        return activityTypeTrackerMap;
    }
}
