package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ActivityTour extends Goal implements Cloneable {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private List<Activity> tour;
    private final DayOfWeek day;
    private final long pid;
    private final long hid;

    public ActivityTour(long pid, long hid, DayOfWeek day) {
        this.pid = pid;
        this.hid = hid;
        this.day = day;
        this.tour = new ArrayList<Activity>();
    }
    public ActivityTour(long pid, long hid, DayOfWeek day, List<Activity> tour) {
        this.pid = pid;
        this.hid = hid;
        this.day = day;
        this.tour = tour;
    }

    @Override
    public boolean isAchieved(AgentContextInterface agentContextInterface) {
        // Activity should never be associated with a plan as a goal, and should never be achieved
        return false;
    }

    @Override
    public String toString() {
        // start_time, duration, location, mask_state, disease_state

        return String.format(
                "%s (%s) %s - %s",
                this.tour
        );
    }

    @Override
    public ActivityTour clone() {
        return new ActivityTour(this.pid, this.hid, this.day, this.tour);
    }

    public void addActivity(Activity activity) {
        this.tour.add(activity);
    }

    public List<Activity> getActivityTour() {return this.tour;}
    public DayOfWeek getDay() {return this.day;}
    public long getPid() {return this.pid;}
    public long getHid() {return this.hid;}

}
