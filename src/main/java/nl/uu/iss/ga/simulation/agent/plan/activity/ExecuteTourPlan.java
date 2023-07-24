package main.java.nl.uu.iss.ga.simulation.agent.plan.activity;

import main.java.nl.uu.iss.ga.model.data.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;
import main.java.nl.uu.iss.ga.simulation.agent.context.BeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingBusBeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingSimmetricBeliefContext;
import main.java.nl.uu.iss.ga.simulation.agent.context.RoutingTrainBeliefContext;
import main.java.nl.uu.iss.ga.util.CumulativeDistribution;
import main.java.nl.uu.iss.ga.util.MNLModalChoiceModel;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.util.HashMap;
import java.util.List;


public class ExecuteTourPlan extends RunOncePlan<TripTour> {
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
        List<Activity> activities = activityTour.getActivityTour();
        this.tripTour = new TripTour(activityTour.getPid(), activityTour.getDay());
        Person person = planToAgentInterface.getContext(Person.class);

        // only agents that are older than 4 years old can decide their activities
        if(person.getAge() >=4 ){

            BeliefContext beliefContext = planToAgentInterface.getContext(BeliefContext.class);

            MNLModalChoiceModel modalChoiceModel = planToAgentInterface.getContext(MNLModalChoiceModel.class);

            HashMap<TransportMode, Double> travelTimes = new HashMap<TransportMode, Double>();
            HashMap<TransportMode, Double> travelDistances = new HashMap<TransportMode, Double>();

            int nChangesBus = 0;
            int nChangesTrain = 0;
            double walkTimeBus = 0;
            double walkTimeTrain = 0;
            double busTimeTrain = 0;
            double busDistanceTrain = 0;

            boolean trainPossible = false;
            boolean busPossible = false;
            boolean walkPossible = false;
            boolean bikePossible = false;
            boolean carDriverPossible = false;
            boolean carPassengerPossible = false;

            RoutingSimmetricBeliefContext routingSimmetric = planToAgentInterface.getContext(RoutingSimmetricBeliefContext.class);
            RoutingBusBeliefContext routingBus = planToAgentInterface.getContext(RoutingBusBeliefContext.class);
            RoutingTrainBeliefContext routingTrain = planToAgentInterface.getContext(RoutingTrainBeliefContext.class);

            /**
             *  generation of transport mode for each trip
             */
            Activity activityOrigin = activities.get(0);

            // initialise the trip tour
            for (Activity activityDestination : activities.subList(1, activities.size())) {
                // not entirely outside DHZW and the postcodes are different
                if ((activityOrigin.getLocation().isInDHZW() | activityDestination.getLocation().isInDHZW()) & (!activityOrigin.getLocation().getPostcode().equals(activityDestination.getLocation().getPostcode()))) {
                    TwoStringKeys simmetricPostcodes = new TwoStringKeys(activityOrigin.getLocation().getPostcode(), activityDestination.getLocation().getPostcode());

                    Trip trip = new Trip(this.pid,
                            this.hid,
                            activityOrigin,
                            activityDestination,
                            routingSimmetric.getBeelineDistance(simmetricPostcodes)
                    );
                    this.tripTour.addTrip(trip);
                }
                activityOrigin = activityDestination;
            }

            // sort by euclidean distance
            this.tripTour.sortTripsByDistance();

            // initialise the first mode with walk, so it does not catch the if condition just below
            TransportMode firstMode = TransportMode.WALK;

            // go through the trips
            for (Trip trip : this.tripTour.getTripChain()) {
                // if the first mode was the car driver, the whole chain is by that mode
                if (tripTour.getTripChain().indexOf(trip) != 0 & firstMode.equals(TransportMode.CAR_DRIVER)) {
                    trip.setTransportMode(TransportMode.CAR_DRIVER);

                    String departurePostcode = trip.getDepartureActivity().getLocation().getPostcode();
                    String arrivalPostcode = trip.getArrivalActivity().getLocation().getPostcode();
                    TwoStringKeys simmetricPostcodes = new TwoStringKeys(departurePostcode, arrivalPostcode);
                    double distance = routingSimmetric.getCarDistance(simmetricPostcodes);
                    trip.setDistance(distance);

                    beliefContext.getModeOfTransportTracker().notifyTransportModeUsed(TransportMode.CAR_DRIVER, beliefContext.getToday(), trip.getArrivalActivity().getActivityType(), person.hasCarLicense(), person.getHousehold().hasCarOwnership(), trip.getBeelineDistance());
                } else {
                    // either first trip of the chain, either the car driver was not chosen as first mode

                    String departurePostcode = trip.getDepartureActivity().getLocation().getPostcode();
                    String arrivalPostcode = trip.getArrivalActivity().getLocation().getPostcode();
                    boolean departureInDHZW = trip.getDepartureActivity().getLocation().isInDHZW();
                    boolean arrivalInDHZW = trip.getArrivalActivity().getLocation().isInDHZW();

                    // reset flags for the upcoming trip
                    travelTimes.clear();
                    travelDistances.clear();

                    TwoStringKeys simmetricPostcodes = new TwoStringKeys(departurePostcode, arrivalPostcode);

                    if (routingSimmetric.getWalkTime(simmetricPostcodes) == -1.0) {
                        walkPossible = false;
                    } else {
                        walkPossible = true;
                        travelTimes.put(TransportMode.WALK, routingSimmetric.getWalkTime(simmetricPostcodes));
                        travelDistances.put(TransportMode.WALK, routingSimmetric.getWalkDistance(simmetricPostcodes));
                    }

                    if (routingSimmetric.getBikeTime(simmetricPostcodes) == -1.0) {
                        bikePossible = false;
                    } else {
                        bikePossible = true;
                        travelTimes.put(TransportMode.BIKE, routingSimmetric.getBikeTime(simmetricPostcodes));
                        travelDistances.put(TransportMode.BIKE, routingSimmetric.getWalkDistance(simmetricPostcodes));
                    }

                    // if trip is feasible by car and the household has a car, the agent can be passenger
                    if (routingSimmetric.getCarTime(simmetricPostcodes) != -1.0 & person.getHousehold().hasCarOwnership()) {
                        carPassengerPossible = true;
                        travelTimes.put(TransportMode.CAR_PASSENGER, routingSimmetric.getCarTime(simmetricPostcodes));
                        travelDistances.put(TransportMode.CAR_PASSENGER, routingSimmetric.getCarDistance(simmetricPostcodes));
                    } else {
                        carPassengerPossible = false;
                    }

                    // car is either chosen at the beginning or never anymore. If it is taken at the first round, it is automatically applied to all the other trips.
                    carDriverPossible = false;
                    if (tripTour.getTripChain().indexOf(trip) == 0 & person.hasCarLicense() & person.getHousehold().hasCarOwnership() & routingSimmetric.getCarTime(simmetricPostcodes) != -1.0) {
                        carDriverPossible = true;
                        travelTimes.put(TransportMode.CAR_DRIVER, routingSimmetric.getCarTime(simmetricPostcodes));
                        travelDistances.put(TransportMode.CAR_DRIVER, routingSimmetric.getCarDistance(simmetricPostcodes));
                    }

                    // if the bus is possible
                    busPossible = routingBus.getFeasibleFlag(departurePostcode, arrivalPostcode) != -1;
                    if (busPossible) {
                        travelTimes.put(TransportMode.BUS_TRAM, routingBus.getBusTime(departurePostcode, arrivalPostcode));
                        travelDistances.put(TransportMode.BUS_TRAM, routingBus.getBusDistance(departurePostcode, arrivalPostcode));
                        nChangesBus = routingBus.getChange(departurePostcode, arrivalPostcode);
                        walkTimeBus = routingBus.getWalkTime(departurePostcode, arrivalPostcode);
                    }

                    // if the trip is partially outside, the train could be possible
                    if (departureInDHZW ^ arrivalInDHZW) {   // XOR operator
                        trainPossible = routingTrain.getFeasibleFlag(departurePostcode, arrivalPostcode) != -1;
                        if (trainPossible) {
                            travelTimes.put(TransportMode.TRAIN, routingTrain.getTrainTime(departurePostcode, arrivalPostcode));
                            travelDistances.put(TransportMode.TRAIN, routingTrain.getTrainDistance(departurePostcode, arrivalPostcode));
                            nChangesTrain = routingTrain.getChange(departurePostcode, arrivalPostcode);
                            walkTimeTrain = routingTrain.getWalkTime(departurePostcode, arrivalPostcode);
                            busTimeTrain = routingTrain.getBusTime(departurePostcode, arrivalPostcode);
                            busDistanceTrain = routingTrain.getBusDistance(departurePostcode, arrivalPostcode);
                        }
                    }

                    // compute choice probabilities
                    HashMap<TransportMode, Double> choiceProbabilities = modalChoiceModel.getChoiceProbabilities(
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
                            nChangesTrain
                    );

                    // decide the modal choice
                    TransportMode transportMode = CumulativeDistribution.sampleWithCumulativeDistribution(choiceProbabilities);
                    trip.setTransportMode(transportMode);

                    double distance = 0;
                    if(transportMode.equals(TransportMode.WALK) | transportMode.equals(TransportMode.BIKE) | transportMode.equals(TransportMode.CAR_PASSENGER) | transportMode.equals(TransportMode.CAR_DRIVER)) {
                        distance = travelDistances.get(transportMode);
                    } else if (transportMode.equals(TransportMode.BUS_TRAM)) {
                        distance = routingBus.getTotalDistance(departurePostcode, arrivalPostcode);
                    } else {
                        distance = routingTrain.getTotalDistance(departurePostcode, arrivalPostcode);
                    }
                    trip.setDistance(distance);

                    beliefContext.getModeOfTransportTracker().notifyTransportModeUsed(transportMode, beliefContext.getToday(), trip.getArrivalActivity().getActivityType(), person.hasCarLicense(), person.getHousehold().hasCarOwnership(), trip.getDistance());

                    if (tripTour.getTripChain().indexOf(trip) == 0) {
                        firstMode = transportMode;
                    }

                }

            }

        }

        return this.tripTour;
    }

}
