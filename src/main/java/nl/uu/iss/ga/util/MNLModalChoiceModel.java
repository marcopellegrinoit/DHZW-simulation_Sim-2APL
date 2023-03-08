package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MNLModalChoiceModel {
    private static final HashMap<TransportMode, Integer> alpha = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaTime = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaCost = new HashMap<>();

    private static final double betaTimeWalkBus = -0.03;
    private static final double betaTimeWalkTrain = -0.03;
    private static final double betaChangesBus = -0.3;
    private static final double betaChangesTrain = -0.3;

    /**
     * Initialise all the alpha and beta constants
     */
    static {
        alpha.put(TransportMode.WALK, 0);
        alpha.put(TransportMode.BIKE, 0);
        alpha.put(TransportMode.CAR_DRIVER, 1);
        alpha.put(TransportMode.CAR_PASSENGER, -3);
        alpha.put(TransportMode.BUS_TRAM, -1);
        alpha.put(TransportMode.TRAIN, -1);

        betaTime.put(TransportMode.WALK, -0.04);
        betaTime.put(TransportMode.BIKE, -0.03);
        betaTime.put(TransportMode.CAR_DRIVER, -0.02);
        betaTime.put(TransportMode.CAR_PASSENGER, -0.02);
        betaTime.put(TransportMode.BUS_TRAM, -0.02);
        betaTime.put(TransportMode.TRAIN, -0.02);

        betaCost.put(TransportMode.CAR_DRIVER, -0.15);
        betaCost.put(TransportMode.CAR_PASSENGER, -0.15);
        betaCost.put(TransportMode.BUS_TRAM, -0.1);
        betaCost.put(TransportMode.TRAIN, -0.1);
    }

    /**
     * @param travelTimes: map of trip duration with such transport mode
     * @param costs: map of costs with such transport mode
     * @param nChangesBus: number of changes if using the bus
     * @return a map of probability distribution over transport modes
     */
    public static HashMap<TransportMode, Double> getChoiceProbabilities (HashMap<TransportMode, Integer> travelTimes, HashMap<TransportMode, Double> costs, int nChangesBus, int nChangesTrain, boolean carPossible, boolean trainPossible, boolean busTramPossible, boolean walkPossible) {
        // probability distribution of transport modes
        HashMap<TransportMode, Double> choiceProbabilities = new HashMap<>();

        double sumUtilitiesExp = 0.0;

        // add the transport modes that are always available
        List<TransportMode> transportModeList = new ArrayList<TransportMode>();
        transportModeList.add(TransportMode.BIKE);
        transportModeList.add(TransportMode.CAR_PASSENGER);

        // add the transport modes that might be available
        if (walkPossible){
            transportModeList.add(TransportMode.WALK);
        }
        if (trainPossible){
            transportModeList.add(TransportMode.TRAIN);
        }
        if (carPossible){
            transportModeList.add(TransportMode.CAR_DRIVER);
        }
        if (busTramPossible){
            transportModeList.add(TransportMode.BUS_TRAM);
        }

        // iterate for the transport modes available for such trip and calculate its utility
        for (TransportMode transportMode: transportModeList){
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
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case CAR_PASSENGER:
                    utility_i = alpha.get(transportMode) + betaTime.get(transportMode) * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode);
                    break;
                case BUS_TRAM:
                    utility_i = alpha.get(transportMode) + betaTimeWalkBus * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode) + betaChangesBus * nChangesBus;
                    break;
                case TRAIN:
                    utility_i = alpha.get(transportMode) + betaTimeWalkTrain * travelTimes.get(transportMode) + betaCost.get(transportMode) * costs.get(transportMode) + betaChangesTrain * nChangesTrain;
                    break;
                default:
                    break;
            }

            // compute probability choices for the modes available.
            double utilityExp_i = Math.exp(utility_i);   // e^(utility)
            choiceProbabilities.put(transportMode, utilityExp_i);  // save e^(utility)
            sumUtilitiesExp += utilityExp_i;  // update the sum of e^(utility)
        }

        // update map by dividing the exponential utilities by their sum
        for (TransportMode transportMode: choiceProbabilities.keySet()){
            choiceProbabilities.put(transportMode, choiceProbabilities.get(transportMode) / sumUtilitiesExp);
        }

        return choiceProbabilities;
    }

}
