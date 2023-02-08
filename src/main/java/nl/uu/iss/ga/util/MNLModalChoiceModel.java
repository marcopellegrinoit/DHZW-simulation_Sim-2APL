package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.HashMap;

public class MNLModalChoiceModel {
    private static final HashMap<TransportMode, Integer> alpha = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaTime = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaCost = new HashMap<>();

    private static final double betaTimeWalkBus = -0.03;
    private static final double betaChangesBus = -0.3;

    /**
     * Initialise all the alpha and beta constants
     */
    static {
        alpha.put(TransportMode.WALK, 0);
        alpha.put(TransportMode.BIKE, 0);
        alpha.put(TransportMode.CAR_DRIVER, 1);
        alpha.put(TransportMode.CAR_PASSENGER, -3);
        alpha.put(TransportMode.BUS_TRAM, -1);

        betaTime.put(TransportMode.WALK, -0.04);
        betaTime.put(TransportMode.BIKE, -0.03);
        betaTime.put(TransportMode.CAR_DRIVER, -0.02);
        betaTime.put(TransportMode.CAR_PASSENGER, -0.02);
        betaTime.put(TransportMode.BUS_TRAM, -0.02);

        betaCost.put(TransportMode.CAR_DRIVER, -0.15);
        betaCost.put(TransportMode.CAR_PASSENGER, -0.15);
        betaCost.put(TransportMode.BUS_TRAM, -0.1);
    }

    /**
     * @param travelTimeSeconds: map of trip duration with such transport mode
     * @param costs: map of costs with such transport mode
     * @param nChangesBus: number of changes if using the bus
     * @return a map of probability distribution over transport modes
     */
    public HashMap<TransportMode, Double> TransportMode (HashMap<TransportMode, Integer> travelTimeSeconds, HashMap<TransportMode, Double> costs, int nChangesBus) {
        // probability distribution of transport modes
        HashMap<TransportMode, Double> probDistribution = new HashMap<>();

        double sumUtilitiesExp = 0;
        // estimate a utility for each transport mode
        for (TransportMode transportMode: TransportMode.values()){
            double utility_i = 0;

            // can be optimized: walk + bike and car_driver + car_passenger
            switch (transportMode){
                case WALK:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimeSeconds.get(transportMode);
                    break;
                case BIKE:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimeSeconds.get(transportMode);
                    break;
                case CAR_DRIVER:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimeSeconds.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case CAR_PASSENGER:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimeSeconds.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case BUS_TRAM:
                    utility_i = alpha.get(transportMode) + betaTimeWalkBus * travelTimeSeconds.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode) + betaChangesBus * nChangesBus;
                    break;
                default:
                    break;
            }

            double utilityExp_i = Math.pow(utility_i, 2);   // exponential of the utility
            probDistribution.put(transportMode, utilityExp_i);  // save the exponential utility
            sumUtilitiesExp = sumUtilitiesExp + utilityExp_i;  // update the sum of exponential utilities
        }

        // update map by dividing the exponential utilities by their sum
        for (TransportMode transportMode: probDistribution.keySet()){
            probDistribution.put(transportMode, probDistribution.get(transportMode) / sumUtilitiesExp);
        }

        return probDistribution;
    }

}
