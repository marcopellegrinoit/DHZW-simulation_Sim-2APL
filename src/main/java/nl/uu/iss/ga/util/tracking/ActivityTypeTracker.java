package main.java.nl.uu.iss.ga.util.tracking;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
