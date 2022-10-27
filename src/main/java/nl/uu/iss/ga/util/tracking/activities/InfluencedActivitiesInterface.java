package main.java.nl.uu.iss.ga.util.tracking.activities;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface InfluencedActivitiesInterface {
    int getDeltaCases();

    int getnInfected();

    void setNInfected(int nInfected);

    long getTick();


    void activityContinuing(Activity activity);

    Map<ActivityType, Integer> getCancelledActivities();

    Map<ActivityType, Integer> getContinuedActivities();

    Map<ActivityType, Integer> getTotalActivities();

    int getSumTotalActivities();

    Map<ActivityType, Double> getFractionActivitiesCancelled();

    default <T> Map<T, Integer> atomicIntMapToIntMap(Map<T, AtomicInteger> map) {
        Map<T, Integer> newMap = new HashMap<>();
        for(T key : map.keySet()) {
            newMap.put(key, map.get(key).intValue());
        }
        return newMap;
    }
}
