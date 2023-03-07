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
     * @param travelTimes: map of trip duration with such transport mode
     * @param costs: map of costs with such transport mode
     * @param nChangesBus: number of changes if using the bus
     * @return a map of probability distribution over transport modes
     */
    public static HashMap<TransportMode, Double> getChoiceProbabilities (HashMap<TransportMode, Integer> travelTimes, HashMap<TransportMode, Double> costs, int nChangesBus, boolean carPossible, boolean trainPossible, boolean busTramPossible) {
        // probability distribution of transport modes
        HashMap<TransportMode, Double> choiceProbabilities = new HashMap<>();

        double sumUtilitiesExp = 0.0;
        // estimate a utility for each transport mode
        for (TransportMode transportMode: TransportMode.values()){
            double utility_i = 0.0;

            // can be optimized: walk + bike and car_driver + car_passenger
            switch (transportMode){
                case WALK:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(transportMode);
                    break;
                case BIKE:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(transportMode);
                    break;
                case CAR_DRIVER:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(TransportMode.CAR) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case CAR_PASSENGER:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(TransportMode.CAR) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case BUS_TRAM:
                    utility_i = alpha.get(transportMode) + betaTimeWalkBus * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode) + betaChangesBus * nChangesBus;
                    break;
                case TRAIN:
                    utility_i = alpha.get(transportMode) + betaTimeWalkBus * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode) + betaChangesBus * nChangesBus;
                    break;
                default:
                    break;
            }

            // compute probability choices for the modes available. walk, bike and car passenger are always available
            if (!((transportMode.equals(TransportMode.TRAIN) & !trainPossible) | (transportMode.equals(TransportMode.BUS_TRAM) & !busTramPossible) | (transportMode.equals(TransportMode.CAR_DRIVER) & !carPossible))){
                double utilityExp_i = Math.exp(utility_i);   // e^(utility)
                choiceProbabilities.put(transportMode, utilityExp_i);  // save e^(utility)
                sumUtilitiesExp += utilityExp_i;  // update the sum of e^(utility)
            }
        }

        // update map by dividing the exponential utilities by their sum
        for (TransportMode transportMode: choiceProbabilities.keySet()){
            choiceProbabilities.put(transportMode, choiceProbabilities.get(transportMode) / sumUtilitiesExp);
        }

        return choiceProbabilities;
    }

}
