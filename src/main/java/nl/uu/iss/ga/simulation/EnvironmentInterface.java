package main.java.nl.uu.iss.ga.simulation;

import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.Simulation;
import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.CodeTypeInterface;
import main.java.nl.uu.iss.ga.util.config.ArgParse;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.deliberation.DeliberationResult;
import nl.uu.cs.iss.ga.sim2apl.core.platform.Platform;
import nl.uu.cs.iss.ga.sim2apl.core.tick.TickHookProcessor;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvironmentInterface implements TickHookProcessor<Activity> {

    private final boolean printOutput = false;

    private static final Logger LOGGER = Logger.getLogger(EnvironmentInterface.class.getName());
    private final Platform platform;
    private final ArgParse arguments;
    private long currentTick = 0;
    private LocalDateTime simulationStarted;
    private final LocalDate startDate;
    private DayOfWeek today = DayOfWeek.MONDAY;

    private ModeOfTransportTracker modeOfTransportTracker;
    private ActivityTypeTracker activityTypeTracker;

    public EnvironmentInterface(
            Platform platform,
            ArgParse arguments,
            ModeOfTransportTracker modeOfTransportTracker,
            ActivityTypeTracker activityTypeTracker
    ) {
        this.platform = platform;
        this.arguments = arguments;
        this.modeOfTransportTracker = modeOfTransportTracker;
        this.activityTypeTracker = activityTypeTracker;

        this.startDate = arguments.getStartdate();

        if (this.startDate != null) {
            this.today = DayOfWeek.fromDate(this.startDate);
            LOGGER.log(Level.INFO, "Start date set to " + this.startDate.format(DateTimeFormatter.ofPattern("cccc dd MMMM yyyy")));
        }
    }

    public void setSimulationStarted() {
        this.simulationStarted = LocalDateTime.now();
        modeOfTransportTracker.reset();
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public DayOfWeek getToday() {
        return today;
    }

    @Override
    public void tickPreHook(long tick) {
        this.currentTick = tick;
        if (this.startDate == null) {
            this.today = CodeTypeInterface.parseAsEnum(DayOfWeek.class, (int) (currentTick % 7 + 1));
        } else {
            this.today = DayOfWeek.fromDate(this.startDate.plusDays(tick));
        }

        String date = this.startDate.plusDays(tick).format(DateTimeFormatter.ISO_DATE);

        //modeOfTransportTracker.reset();
        activityTypeTracker.reset();
    }

    @Override
    public void tickPostHook(long tick, int lastTickDuration, List<Future<DeliberationResult<Activity>>> agentActions) {
        LOGGER.log(Level.FINE, String.format(
                "Tick %d took %d milliseconds for %d agents (roughly %fms per agent)",
                tick, lastTickDuration, agentActions.size(), (double) lastTickDuration / agentActions.size()));
    }

    @Override
    public void simulationFinishedHook(long l, int i) {
        Duration startupDuration = Duration.between(Simulation.instantiated, this.simulationStarted);
        Duration simulationDuration = Duration.between(this.simulationStarted, LocalDateTime.now());
        Duration combinedDuration = Duration.between(Simulation.instantiated, LocalDateTime.now());
        LOGGER.log(Level.INFO, String.format(
                "Simulation finished.\n\tInitialization took\t\t%s.\n\tSimulation took\t\t\t%s for %d time steps.\n\tTotal simulation time:\t%s",
                prettyPrint(startupDuration),
                prettyPrint(simulationDuration),
                l,
                prettyPrint(combinedDuration))
        );

        if (printOutput) {
            System.out.println("\nOverall mode choices:");
            System.out.println(modeOfTransportTracker.getTotalModeMap());

            System.out.println("\nMode x day:");
            AtomicInteger[][] modeDayMap = modeOfTransportTracker.getModeDayMap();
            for (DayOfWeek day : DayOfWeek.values()) {
                System.out.println(day + ":");
                for (TransportMode mode : TransportMode.values()) {
                    System.out.println(" " + mode + ": " + modeDayMap[day.ordinal()][mode.ordinal()]);
                }
            }

            System.out.println("\nMode x activity:");
            AtomicInteger[][] modeActivityMap = modeOfTransportTracker.getModeActivityMap();
            for (ActivityType activity : ActivityType.values()) {
                System.out.println(activity + ":");
                for (TransportMode mode : TransportMode.values()) {
                    System.out.println(" " + mode + ": " + modeActivityMap[activity.ordinal()][mode.ordinal()]);
                }
            }

            System.out.println("\nMode x car license:");
            AtomicInteger[][] modeCarLicenseMap = modeOfTransportTracker.getModeCarLicenseMap();
            for (boolean b : new boolean[]{true, false}) {
                System.out.println(b + ":");
                for (TransportMode mode : TransportMode.values()) {
                    System.out.println(" " + mode + ": " + modeCarLicenseMap[b ? 1 : 0][mode.ordinal()]);
                }
            }

            System.out.println("\nMode x car ownership:");
            AtomicInteger[][] modeCarOwnership = modeOfTransportTracker.getModeCarOwnershipMap();
            for (boolean b : new boolean[]{true, false}) {
                System.out.println(b + ":");
                for (TransportMode mode : TransportMode.values()) {
                    System.out.println(" " + mode + ": " + modeCarOwnership[b ? 1 : 0][mode.ordinal()]);
                }
            }
        }

        try {
            modeOfTransportTracker.appendOutput(this.arguments.getOutputFile());
            modeOfTransportTracker.saveTotalModeToCsv(new File("src/main/resources/distance_analysis/"));
            modeOfTransportTracker.saveDistanceToCsv(new File("src/main/resources/distance_analysis/"));
/*          modeOfTransportTracker.saveModeDayToCsv(new File(this.arguments.getOutputDir()));
            modeOfTransportTracker.saveModeActivityToCsv(new File(this.arguments.getOutputDir()));
            modeOfTransportTracker.saveModeCarLicenseToCsv(new File(this.arguments.getOutputDir()));
            modeOfTransportTracker.saveModeCarOwnershipToCsv(new File(this.arguments.getOutputDir()));*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * From @url{https://stackoverflow.com/questions/3471397/how-can-i-pretty-print-a-duration-in-java#answer-16323209}
     *
     * @param duration Duration object to pretty print
     * @return Pretty printed duration
     */
    private String prettyPrint(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }


}
