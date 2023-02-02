package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.LocationEntry;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ActivityChain extends Goal implements Cloneable {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private List<Activity> chain;
    private final DayOfWeek day;
    private final long pid;

    public ActivityChain(long pid, DayOfWeek day) {
        this.pid = pid;
        this.day = day;
        this.chain = new ArrayList<Activity>();
    }
    public ActivityChain(long pid, DayOfWeek day, List<Activity> chain) {
        this.pid = pid;
        this.day = day;
        this.chain = chain;
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
                this.chain
        );
    }

    @Override
    public ActivityChain clone() {
        return new ActivityChain(this.pid, this.day, this.chain);
    }

    public void addActivity(Activity activity) {
        this.chain.add(activity);
    }

    public List<Activity> getActivityChain() {return this.chain;}
    public DayOfWeek getDay() {return this.day;}
    public long getPid() {return this.pid;}
}
