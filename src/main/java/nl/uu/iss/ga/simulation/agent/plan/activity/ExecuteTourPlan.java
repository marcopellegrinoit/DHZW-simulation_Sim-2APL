package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingBusBeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingSimmetricBeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingTrainBeliefContext;
import main.java.nl.uu.iss.ga.util.CumulativeDistribution;
import main.java.nl.uu.iss.ga.util.MNLModalChoiceModel;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExecuteTourPlan extends RunOncePlan<TripTour> {
    private static final Logger LOGGER = Logger.getLogger(ExecuteTourPlan.class.getName());
    private final ActivityTour activityTour;
    private TripTour tripTour;
    private final long pid;
    private final long hid;

    public ExecuteTourPlan(ActivityTour activityTour) {
        this.activityTour = activityTour;
        this.pid = activityTour.getPid();
        this.hid = activityTour.getHid();
    }

    /**
     * This function is called at every midnight for each action of the next day.
     *
     * @param planToAgentInterface
     * @return
     */
    @Override
    public TripTour executeOnce(PlanToAgentInterface<TripTour> planToAgentInterface) {
        HashMap<TransportMode, Double> travelTimes = new HashMap<TransportMode, Double>();
        HashMap<TransportMode, Double> travelDistances = new HashMap<TransportMode, Double>();

        int nChangesBus = 0;
        int nChangesTrain = 0;
        double walkTimeBus = 0;
        double walkTimeTrain = 0;
        double busTimeTrain = 0;
        double busDistanceTrain = 0;
        String postcodeStopBus = null;
        String postcodeStopTrain = null;

        boolean trainPossible  = false;
        boolean busPossible = false;
        boolean walkPossible  = false;
        boolean bikePossible = false;
        boolean carDriverPossible = false;
        boolean carPassengerPossible = false;

        Person person = planToAgentInterface.getContext(Person.class);

        RoutingSimmetricBeliefContext routingSimmetric = planToAgentInterface.getContext(RoutingSimmetricBeliefContext.class);
        RoutingBusBeliefContext routingBus = planToAgentInterface.getContext(RoutingBusBeliefContext.class);
        RoutingTrainBeliefContext routingTrain = planToAgentInterface.getContext(RoutingTrainBeliefContext.class);

        /**
         *  generation of transport mode for each trip
         */
        List<Activity> activities = activityTour.getActivityTour();

        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Activity activityOrigin = activities.get(0);

        // for each destination (starting from the second one) there is a trip
        for (Activity activityDestination : activities.subList(1, activities.size())) {
            // not entirely outside DHZW and the postcodes are different
            if ((activityOrigin.getLocation().isInDHZW() | activityDestination.getLocation().isInDHZW()) & (!activityOrigin.getLocation().getPostcode().equals(activityDestination.getLocation().getPostcode()))) {
                // reset flags for the upcoming trip
                travelTimes.clear();
                travelDistances.clear();
                carDriverPossible = person.hasCarLicense() & person.getHousehold().hasCarOwnership();

                TwoStringKeys simmetricPostcodes = new TwoStringKeys(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());

                if(routingSimmetric.getWalkTime(simmetricPostcodes) == -1.0) {
                    walkPossible = false;
                } else {
                    walkPossible = true;
                    travelTimes.put(TransportMode.WALK, routingSimmetric.getWalkTime(simmetricPostcodes));
                    travelDistances.put(TransportMode.WALK, routingSimmetric.getWalkDistance(simmetricPostcodes));
                }

                if(routingSimmetric.getBikeTime(simmetricPostcodes) == -1.0) {
                    bikePossible = false;
                } else {
                    bikePossible = true;
                    travelTimes.put(TransportMode.BIKE, routingSimmetric.getBikeTime(simmetricPostcodes));
                    travelDistances.put(TransportMode.BIKE, routingSimmetric.getBikeDistance(simmetricPostcodes));
                }

                if(routingSimmetric.getCarTime(simmetricPostcodes) == -1.0) {
                    carPassengerPossible = false;
                } else {
                    carPassengerPossible = true;
                    travelTimes.put(TransportMode.CAR_PASSENGER, routingSimmetric.getCarTime(simmetricPostcodes));
                    travelDistances.put(TransportMode.CAR_PASSENGER, routingSimmetric.getCarDistance(simmetricPostcodes));
                }

                if(carDriverPossible) {
                    // check if the trip is feasible by car
                    if(carPassengerPossible) {
                        travelTimes.put(TransportMode.CAR_DRIVER, routingSimmetric.getCarTime(simmetricPostcodes));
                        travelDistances.put(TransportMode.CAR_DRIVER, routingSimmetric.getCarDistance(simmetricPostcodes));
                    } else {
                        carDriverPossible = false;
                    }
                }


                // if the bus is possible
                busPossible = routingBus.getFeasibleFlag(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()) != -1;
                if(busPossible) {
                    travelTimes.put(TransportMode.BUS_TRAM, routingBus.getBusTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()));
                    travelDistances.put(TransportMode.BUS_TRAM, routingBus.getBusDistance(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()));
                    nChangesBus = routingBus.getChange(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                    walkTimeBus = routingBus.getWalkTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                    postcodeStopBus = routingBus.getPostcodeDHZW(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                }

                /*
                // if the trip is partially outside, the train could be possible
                if (!(activityOrigin.getLocation().isInDHZW() & activityDestination.getLocation().isInDHZW())) {
                    if(routingTrain.getTrainTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()) == -1.0) {
                        trainPossible = true;
                        travelTimes.put(TransportMode.TRAIN, routingTrain.getTrainTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()));
                        travelDistances.put(TransportMode.TRAIN, routingTrain.getTrainDistance(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode()));
                        nChangesTrain = routingTrain.getChange(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                        walkTimeTrain = routingTrain.getWalkTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                        busTimeTrain = routingTrain.getBusTime(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                        busDistanceTrain = routingTrain.getBusDistance(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                        postcodeStopTrain = routingTrain.getPostcodeDHZW(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());
                    } else {
                        trainPossible = false;
                    }
                }*/

                // compute choice probabilities
                HashMap<TransportMode, Double> choiceProbabilities = MNLModalChoiceModel.getChoiceProbabilities(
                        walkPossible,
                        bikePossible,
                        carDriverPossible,
                        carPassengerPossible,
                        busPossible,
                        trainPossible,
                        travelTimes,
                        travelDistances,
                        walkTimeBus,
                        nChangesBus,
                        walkTimeTrain,
                        busTimeTrain,
                        busDistanceTrain,
                        nChangesTrain,
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType());

                // decide the modal choice
                TransportMode transportMode = CumulativeDistribution.sampleWithCumulativeDistribution(choiceProbabilities);

                // add the trip to the tour
                Trip trip = new Trip(this.pid,
                        this.hid,
                        activityOrigin.getActivityType(),
                        activityDestination.getActivityType(),
                        activityOrigin.getLocation().getPostcode(),
                        activityDestination.getLocation().getPostcode(),
                        activityOrigin.getStartTime(),
                        activityOrigin.getEndTime(),
                        activityDestination.getStartTime(),
                        transportMode);

                this.tripTour.addTrip(trip);

                LOGGER.log(Level.INFO, trip.toString());
            }
            // update past activity
            activityOrigin = activityDestination;
        }

        return this.tripTour;
    }

}
