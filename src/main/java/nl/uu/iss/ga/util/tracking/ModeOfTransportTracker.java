package main.java.nl.uu.iss.ga.util.tracking;

import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ModeOfTransportTracker {

    private Map<TransportMode, AtomicInteger> modeTrackerMap;

    public void reset() {
        modeTrackerMap = new ConcurrentHashMap<>();
        for(TransportMode mode : TransportMode.values()) {
            modeTrackerMap.put(mode, new AtomicInteger());
        }
    }

    public void notifyTransportModeUsed(TransportMode mode) {
        modeTrackerMap.get(mode).getAndIncrement();
    }

    public Map<TransportMode, AtomicInteger> getModeTrackerMap() {
        return modeTrackerMap;
    }
}
