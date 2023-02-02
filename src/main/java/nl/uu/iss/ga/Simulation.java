package main.java.nl.uu.iss.ga;

import main.java.nl.uu.iss.ga.model.data.Activity;
import main.java.nl.uu.iss.ga.simulation.DefaultTimingSimulationEngine;
import main.java.nl.uu.iss.ga.simulation.EnvironmentInterface;
import main.java.nl.uu.iss.ga.simulation.NoRescheduleBlockingTickExecutor;
import main.java.nl.uu.iss.ga.util.Java2APLLogger;
import main.java.nl.uu.iss.ga.util.config.ArgParse;
import main.java.nl.uu.iss.ga.util.config.ConfigModel;
import main.java.nl.uu.iss.ga.util.tracking.ActivityTypeTracker;
import main.java.nl.uu.iss.ga.util.tracking.ModeOfTransportTracker;
import nl.uu.cs.iss.ga.sim2apl.core.defaults.messenger.DefaultMessenger;
import nl.uu.cs.iss.ga.sim2apl.core.platform.Platform;
import nl.uu.cs.iss.ga.sim2apl.core.tick.SimulationEngine;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class Simulation {
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    public static final LocalDateTime instantiated = LocalDateTime.now();

    public static void main(String[] args) {
        ArgParse parser = new ArgParse(args);
        new Simulation(parser);
    }

    private final ArgParse arguments;

    private Platform platform;
    private NoRescheduleBlockingTickExecutor<Activity> tickExecutor;
    private ModeOfTransportTracker modeOfTransportTracker = new ModeOfTransportTracker();
    private ActivityTypeTracker activityTypeTracker = new ActivityTypeTracker();
    private EnvironmentInterface environmentInterface;
    private SimulationEngine<Activity> simulationEngine;

    public Simulation(ArgParse arguments) {
        this.arguments = arguments;
        this.tickExecutor = new NoRescheduleBlockingTickExecutor<>(this.arguments.getThreads(), this.arguments.getSystemWideRandom());

        arguments.getConfigModel().loadFiles();
        preparePlatform();

        ConfigModel configModel = this.arguments.getConfigModel();
        configModel.createAgents(this.platform, this.environmentInterface, modeOfTransportTracker, activityTypeTracker);

        this.environmentInterface.setSimulationStarted();
        this.simulationEngine.start();
    }

    private void preparePlatform() {
        DefaultMessenger messenger = new DefaultMessenger();

        this.platform = Platform.newPlatform(tickExecutor, messenger);
        this.platform.setLogger(new Java2APLLogger());
        this.environmentInterface = new EnvironmentInterface(
                platform,
                this.arguments,
                this.modeOfTransportTracker,
                this.activityTypeTracker
        );
        this.simulationEngine = getLocalSimulationEngine();
    }

    private SimulationEngine<Activity> getLocalSimulationEngine() {
//        return new DefaultSimulationEngine<>(this.platform, (int)arguments.getIterations(), this.environmentInterface);
        return new DefaultTimingSimulationEngine<>(this.platform, this.arguments, (int)this.arguments.getIterations(), this.environmentInterface);
    }

}
