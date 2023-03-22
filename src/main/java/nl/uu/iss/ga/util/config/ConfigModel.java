package main.java.nl.uu.iss.ga.util.config;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.ActivityTour;
import main.java.nl.uu.iss.ga.model.data.ActivitySchedule;
import main.java.nl.uu.iss.ga.model.data.TripTour;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;
import main.java.nl.uu.iss.ga.model.reader.*;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingBeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.planscheme.GoalPlanScheme;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Agent;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentArguments;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.platform.Platform;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigModel {

    private static final Logger LOGGER = Logger.getLogger(ConfigModel.class.getName());

    private Random random;
    private final List<AgentID> agents = new ArrayList<>();
    private final List<File> activityFiles;
    private final List<File> householdFiles;
    private final List<File> personFiles;
    private final List<File> locationsFiles;
    private final List<File> routingWalkFiles;
    private final List<File> routingBikeFiles;
    private final List<File> routingCarFiles;
    private final List<File> locationDesignationFiles;
    private final File stateFile;

    private HouseholdReader householdReader;
    private PersonReader personReader;
    private ActivityFileReader activityFileReader;
    private SimmetricRoutingReader routingWalkReader;
    private SimmetricRoutingReader routingBikeReader;
    private SimmetricRoutingReader routingCarReader;

    private final ArgParse arguments;
    private final TomlTable table;
    private final String name;
    private String outputFileName;

    public ConfigModel(ArgParse arguments, String name, TomlTable table) throws Exception {
        this.arguments = arguments;
        this.name = name;
        this.table = table;

        this.activityFiles = getFiles("activities", true);
        this.householdFiles = getFiles("households", true);
        this.personFiles = getFiles("persons", true);
        this.locationsFiles = getFiles("locations", false);

        this.routingWalkFiles = getFiles("routing_walk", true);
        this.routingBikeFiles = getFiles("routing_bike", true);
        this.routingCarFiles = getFiles("routing_car", true);

        this.stateFile = getFile("statefile", false);
        this.locationDesignationFiles = getFiles("locationDesignations", false);

        if(this.table.contains("seed")) {
            this.random = new Random(table.getLong("seed"));
        } else {
            this.random = new Random();
        }

        createOutFileName();
    }

    public void loadFiles() {

        this.householdReader = new HouseholdReader(
                this.householdFiles,
                this.random
        );
        this.personReader = new PersonReader(this.personFiles, this.householdReader.getHouseholds());
        this.activityFileReader = new ActivityFileReader(this.activityFiles);
        this.routingWalkReader = new SimmetricRoutingReader(this.routingWalkFiles);
        this.routingBikeReader = new SimmetricRoutingReader(this.routingBikeFiles);
        this.routingCarReader = new SimmetricRoutingReader(this.routingCarFiles);
    }

    public void createAgents(Platform platform, EnvironmentInterface environmentInterface, ModeOfTransportTracker modeOfTransportTracker, ActivityTypeTracker activityTypeTracker) {
        for (ActivitySchedule schedule : this.activityFileReader.getActivitySchedules()) {
            createAgentFromSchedule(platform, environmentInterface, schedule, modeOfTransportTracker, activityTypeTracker);
        }
    }
    private void createAgentFromSchedule(Platform platform, EnvironmentInterface environmentInterface, ActivitySchedule schedule, ModeOfTransportTracker modeOfTransportTracker, ActivityTypeTracker activityTypeTracker) {
        BeliefContext beliefContext = new BeliefContext(environmentInterface, modeOfTransportTracker, activityTypeTracker);
        RoutingBeliefContext routingBeliefContext = new RoutingBeliefContext(environmentInterface);

        AgentArguments<TripTour> arguments = new AgentArguments<TripTour>()
                .addContext(this.personReader.getPersons().get(schedule.getPid()))
                .addContext(schedule)
                .addContext(beliefContext)
                .addContext(routingBeliefContext)
                .addGoalPlanScheme(new GoalPlanScheme());
        try {
            URI uri = new URI(null, String.format("agent-%04d", schedule.getPid()),
                    platform.getHost(), platform.getPort(), null, null, null);
            AgentID aid = new AgentID(uri);
            Agent<TripTour> agent = new Agent<>(platform, arguments, aid);
            this.agents.add(aid);
            beliefContext.setAgentID(aid);
            routingBeliefContext.setAgentID(aid);

            long pid = schedule.getPid();
            long hid = schedule.getHid();

            List <Activity> weekSchedule = new ArrayList<>(schedule.getSchedule().values());

            List<String> postcodesVisited = new ArrayList<>();

            // loop into days of the week to split activities into each day
            for (DayOfWeek day: DayOfWeek.values()) {
                // collect all activities of today
                List <Activity> activitiesInDay = schedule.getSchedule().values().stream()
                        .filter( c -> c.getStartTime().getDayOfWeek().equals(day))
                        .collect(Collectors.toList());

                // there is a trip only if there are at least two activities
                if(activitiesInDay.size()>1){
                    // initialise the new chain
                    ActivityTour activityTour = new ActivityTour(pid, hid, day);

                    Activity previousActivity = activitiesInDay.get(0);
                    activityTour.addActivity(previousActivity);

                    // loop through all the activities of the day
                    for (Activity nextActivity: activitiesInDay.subList(1, activitiesInDay.size())) {
                        activityTour.addActivity(nextActivity);

                        // add the routing information to the belief
                        if(!previousActivity.getLocation().getPostcode().equals(nextActivity.getLocation().getPostcode()) & (previousActivity.getLocation().isInDHZW() | nextActivity.getLocation().isInDHZW())){
                            TwoStringKeys key = new TwoStringKeys(previousActivity.getLocation().getPostcode(), nextActivity.getLocation().getPostcode());
                            routingBeliefContext.addWalkTime(key, this.routingWalkReader.getTravelTime(key));
                            routingBeliefContext.addBikeTime(key, this.routingBikeReader.getTravelTime(key));
                            routingBeliefContext.addCarTime(key, this.routingCarReader.getTravelTime(key));
                            routingBeliefContext.addWalkDistance(key, this.routingWalkReader.getDistance(key));
                            routingBeliefContext.addBikeDistance(key, this.routingBikeReader.getDistance(key));
                            routingBeliefContext.addCarDistance(key, this.routingCarReader.getDistance(key));
                        }

                        // if it comes back home the tour closes and start a new one
                        if (nextActivity.getActivityType().equals(ActivityType.HOME)){
                            agent.adoptGoal(activityTour);
                            activityTour = new ActivityTour(pid, hid, day);
                            activityTour.addActivity(nextActivity);
                        }

                        previousActivity = nextActivity;
                    }
                }
            }

        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Failed to create AgentID for agent " + schedule.getPid(), e);
        }
    }

    private List<File> getFiles(String key, boolean required) throws Exception {
        List<File> files = new ArrayList<>();
        if(this.table.contains(key)) {
            TomlArray arr = this.table.getArray(key);
            for(int i = 0; i < arr.size(); i++) {
                files.add(ArgParse.findFile(new File(arr.getString(i))));
            }
        } else if (required) {
            throw new Exception(String.format("Missing required key %s", key));
        }
        return files;
    }

    private File getFile(String key, boolean required) throws Exception {
        File f = null;
        if(this.table.contains(key)) {
            f = ArgParse.findFile(new File(this.table.getString(key)));
        } else if (required) {
            throw new Exception(String.format("Missing required key %s", key));
        }
        return f;
    }

    private void createOutFileName() {
        String descriptor = this.arguments.getDescriptor() == null ? "" : this.arguments.getDescriptor();
        if(this.arguments.getDescriptor() != null) {
            if(!(descriptor.startsWith("-") || descriptor.startsWith("_")))
                descriptor = "-" + descriptor;
            if (!(descriptor.endsWith("-") | descriptor.endsWith("_")))
                descriptor += "-";
        }

        this.outputFileName = String.format(
                "radius-of-gyration-%s-%s%s.csv",
                this.name,
                this.arguments.getNode() >= 0 ? "node" + this.arguments.getNode() : "",
                descriptor
        );
    }

    public Random getRandom() {
        return random;
    }

    public List<File> getActivityFiles() {
        return activityFiles;
    }

    public List<File> getHouseholdFiles() {
        return householdFiles;
    }

    public List<File> getPersonFiles() {
        return personFiles;
    }

    public List<File> getLocationsFiles() {
        return locationsFiles;
    }

    public List<AgentID> getAgents() {
        return agents;
    }

    public File getStateFile() {
        return stateFile;
    }

    public HouseholdReader getHouseholdReader() {
        return householdReader;
    }

    public PersonReader getPersonReader() {
        return personReader;
    }


    public ActivityFileReader getActivityFileReader() {
        return activityFileReader;
    }

    public String getName() {
        return name;
    }


    public String getOutFileName() {
        return this.outputFileName;
    }
}
