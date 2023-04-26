package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MNLModalChoiceModel {
    private static final double CAR_COST_KM = 0.49; // https://watkosteenauto.nl/wat-kost-een-auto-per-km/#:~:text=ANWB%20heeft%20een%20berekening%20vrijgegeven,middenklas%20auto%20%E2%82%AC0%2C49.

    // https://www.rrreis.nl/nieuwe-tarieven-en-prijzen-vanaf-1-januari-2023#:~:text=De%20totaalprijs%20voor%20reizen%20op,voor%20RRReis%20wordt%20%E2%82%AC%200%2C196.
    private static final double PUBLIC_TRANSPORT_COST_KM = 0.2;
    private static final double PUBLIC_TRANSPORT_BASE_FEE = 1.08;
    private static final HashMap<TransportMode, Integer> alphaWork = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaTimeWork = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaCostWork = new HashMap<>();
    private static final HashMap<TransportMode, Integer> alphaSchool = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaTimeSchool = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaCostSchool = new HashMap<>();
    private static final HashMap<TransportMode, Integer> alphaLeisure = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaTimeLeisure = new HashMap<>();
    private static final HashMap<TransportMode, Double> betaCostLeisure = new HashMap<>();

    private static final double betaTimeWalkBus = -0.03;
    private static final double betaTimeWalkTrain = -0.03;
    private static final double betaChangesBus = -0.3;
    private static final double betaChangesTrain = -0.3;

    /**
     * Initialise all the alpha and beta constants
     */
    static {
        // work
        alphaWork.put(TransportMode.WALK, 0);
        alphaWork.put(TransportMode.BIKE, 0);
        alphaWork.put(TransportMode.CAR_DRIVER, 1);
        alphaWork.put(TransportMode.CAR_PASSENGER, -3);
        alphaWork.put(TransportMode.BUS_TRAM, -1);
        alphaWork.put(TransportMode.TRAIN, -1);

        betaTimeWork.put(TransportMode.WALK, -0.04);
        betaTimeWork.put(TransportMode.BIKE, -0.03);
        betaTimeWork.put(TransportMode.CAR_DRIVER, -0.02);
        betaTimeWork.put(TransportMode.CAR_PASSENGER, -0.02);
        betaTimeWork.put(TransportMode.BUS_TRAM, -0.02);
        betaTimeWork.put(TransportMode.TRAIN, -0.02);

        betaCostWork.put(TransportMode.CAR_DRIVER, -0.15);
        betaCostWork.put(TransportMode.CAR_PASSENGER, -0.15);
        betaCostWork.put(TransportMode.BUS_TRAM, -0.1);
        betaCostWork.put(TransportMode.TRAIN, -0.1);

        // school
        alphaSchool.put(TransportMode.WALK, 0);
        alphaSchool.put(TransportMode.BIKE, 0);
        alphaSchool.put(TransportMode.CAR_DRIVER, 1);
        alphaSchool.put(TransportMode.CAR_PASSENGER, -3);
        alphaSchool.put(TransportMode.BUS_TRAM, -1);
        alphaSchool.put(TransportMode.TRAIN, -1);

        betaTimeSchool.put(TransportMode.WALK, -0.04);
        betaTimeSchool.put(TransportMode.BIKE, -0.03);
        betaTimeSchool.put(TransportMode.CAR_DRIVER, -0.02);
        betaTimeSchool.put(TransportMode.CAR_PASSENGER, -0.02);
        betaTimeSchool.put(TransportMode.BUS_TRAM, -0.02);
        betaTimeSchool.put(TransportMode.TRAIN, -0.02);

        betaCostSchool.put(TransportMode.CAR_DRIVER, -0.15);
        betaCostSchool.put(TransportMode.CAR_PASSENGER, -0.15);
        betaCostSchool.put(TransportMode.BUS_TRAM, -0.1);
        betaCostSchool.put(TransportMode.TRAIN, -0.1);

        // other: leisure
        alphaLeisure.put(TransportMode.WALK, 0);
        alphaLeisure.put(TransportMode.BIKE, 0);
        alphaLeisure.put(TransportMode.CAR_DRIVER, 1);
        alphaLeisure.put(TransportMode.CAR_PASSENGER, -3);
        alphaLeisure.put(TransportMode.BUS_TRAM, -1);
        alphaLeisure.put(TransportMode.TRAIN, -1);

        betaTimeLeisure.put(TransportMode.WALK, -0.04);
        betaTimeLeisure.put(TransportMode.BIKE, -0.03);
        betaTimeLeisure.put(TransportMode.CAR_DRIVER, -0.02);
        betaTimeLeisure.put(TransportMode.CAR_PASSENGER, -0.02);
        betaTimeLeisure.put(TransportMode.BUS_TRAM, -0.02);
        betaTimeLeisure.put(TransportMode.TRAIN, -0.02);

        betaCostLeisure.put(TransportMode.CAR_DRIVER, -0.15);
        betaCostLeisure.put(TransportMode.CAR_PASSENGER, -0.15);
        betaCostLeisure.put(TransportMode.BUS_TRAM, -0.1);
        betaCostLeisure.put(TransportMode.TRAIN, -0.1);
    }

    public static HashMap<TransportMode, Double> getChoiceProbabilities (
            boolean walkPossible,
            boolean bikePossible,
            boolean carDriverPossible,
            boolean carPassengerPossible,
            boolean busTramPossible,
            boolean trainPossible,
            HashMap<TransportMode, Double> travelTimes,
            HashMap<TransportMode,Double> travelDistances,
            double walkTimeBus,
            int nChangesBus,
            double walkTimeTrain,
            double busTimeTrain,
            double busDistanceTrain,
            int nChangesTrain,
            ActivityType departureType,
            ActivityType arrivalType) {

        // select the coefficients based on the activity type
        HashMap<TransportMode, Integer> alpha = null;
        HashMap<TransportMode, Double> betaTime = null;
        HashMap<TransportMode, Double> betaCost = null;

        if (departureType.equals(ActivityType.WORK) | arrivalType.equals(ActivityType.WORK)) {
            alpha = alphaWork;
            betaTime = betaTimeWork;
            betaCost = betaCostWork;
        } else if (departureType.equals(ActivityType.SCHOOL) | arrivalType.equals(ActivityType.SCHOOL)) {
            alpha = alphaSchool;
            betaTime = betaTimeSchool;
            betaCost = betaCostSchool;
        } else {
            alpha = alphaLeisure;
            betaTime = betaTimeLeisure;
            betaCost = betaCostLeisure;
        }

        // probability distribution of transport modes
        HashMap<TransportMode, Double> choiceProbabilities = new HashMap<>();

        double sumUtilitiesExp = 0.0;

        // add the transport modes that are available
        List<TransportMode> transportModeList = new ArrayList<TransportMode>();
        if(bikePossible) {
            transportModeList.add(TransportMode.BIKE);
        }
        if(carPassengerPossible) {
            transportModeList.add(TransportMode.CAR_PASSENGER);
        }
        if (walkPossible){
            transportModeList.add(TransportMode.WALK);
        }
        if (trainPossible){
            transportModeList.add(TransportMode.TRAIN);
        }
        if (carDriverPossible){
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
                    utility_i = alpha.get(TransportMode.WALK) + betaTime.get(TransportMode.WALK) * travelTimes.get(TransportMode.WALK);
                    break;
                case BIKE:
                    utility_i = alpha.get(TransportMode.BIKE) + betaTime.get(TransportMode.BIKE) * travelTimes.get(TransportMode.BIKE);
                    break;
                case CAR_DRIVER:
                    //utility_i = alpha.get(TransportMode.CAR_DRIVER) + betaTime.get(TransportMode.CAR_DRIVER) * travelTimes.get(TransportMode.CAR_DRIVER);
                    utility_i = alpha.get(TransportMode.CAR_DRIVER) + betaTime.get(TransportMode.CAR_DRIVER) * travelTimes.get(TransportMode.CAR_DRIVER) + betaCost.get(transportMode) * getCarCost(travelDistances.get(TransportMode.CAR_DRIVER));
                    break;
                case CAR_PASSENGER:
                    // utility_i = alpha.get(TransportMode.CAR_PASSENGER) + betaTime.get(TransportMode.CAR_PASSENGER) * travelTimes.get(TransportMode.CAR_PASSENGER);
                    utility_i = alpha.get(TransportMode.CAR_PASSENGER) + betaTime.get(TransportMode.CAR_PASSENGER) * travelTimes.get(TransportMode.CAR_PASSENGER) + betaCost.get(transportMode) * getCarCost(travelDistances.get(TransportMode.CAR_PASSENGER));
                    break;
                case BUS_TRAM:
                    utility_i = alpha.get(TransportMode.BUS_TRAM) +
                            betaTime.get(TransportMode.BUS_TRAM) * travelTimes.get(TransportMode.BUS_TRAM) +
                            betaCost.get(TransportMode.BUS_TRAM) * getPublicTransportCost(travelDistances.get(TransportMode.BUS_TRAM)) +
                            betaTimeWalkBus * walkTimeBus +
                            betaChangesBus * nChangesBus;
                    break;
                case TRAIN:
                    utility_i = alpha.get(TransportMode.TRAIN) +
                            betaTime.get(TransportMode.TRAIN) * travelTimes.get(TransportMode.TRAIN) +
                            betaCost.get(TransportMode.TRAIN) * getPublicTransportCost(travelDistances.get(TransportMode.TRAIN)) +
                            betaTime.get(TransportMode.BUS_TRAM) * busTimeTrain +
                            betaCost.get(TransportMode.BUS_TRAM) * getPublicTransportCost(busDistanceTrain) +
                            betaTimeWalkTrain * walkTimeTrain +
                            betaChangesTrain * nChangesTrain;
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

    private static double getCarCost(double distance) {
        return distance * CAR_COST_KM;
    }
    private static double getPublicTransportCost(double distance) {
        return PUBLIC_TRANSPORT_BASE_FEE + distance * PUBLIC_TRANSPORT_COST_KM;
    }


}
