package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.Person;
import main.java.nl.uu.iss.ga.model.data.Trip;
import main.java.nl.uu.iss.ga.model.data.TripChain;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteChainTripPlan extends RunOncePlan<TripChain> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteChainTripPlan.class.getName());

    private TripChain tripChain;

    public ExecuteChainTripPlan(TripChain tripChain) {
        this.tripChain = tripChain;
    }

    /**
     * This function is called at every midnight for each action of the next day.
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripChain executeOnce(PlanToAgentInterface<TripChain> planToAgentInterface) {
        BeliefContext beliefContext = planToAgentInterface.getContext(BeliefContext.class);
        Person person = planToAgentInterface.getContext(Person.class);

        if (person.getPid() == 84) {
            LOGGER.log(Level.INFO,"ExecuteSchedulesActivityPlan - " + person.getPid() + " - " + this.tripChain.getTripChain().size());
        }

        // for each trip in the chain
        for (Trip trip : this.tripChain.getTripChain()) {
            LOGGER.log(Level.INFO,trip.getPreviousActivityTime() + " - " + trip.getNextActivityTime());

            // here is where I set the modal choice and the times about each trip in the chain
            trip.setDepartureTime(trip.getPreviousActivityTime());
        }

        return tripChain;
    }
}
