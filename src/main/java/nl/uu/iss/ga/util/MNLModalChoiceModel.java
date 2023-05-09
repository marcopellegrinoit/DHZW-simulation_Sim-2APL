package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import main.java.nl.uu.iss.ga.model.reader.MNLparametersReader;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MNLModalChoiceModel implements Context {
    private static final double CAR_COST_KM = 0.49; // https://watkosteenauto.nl/wat-kost-een-auto-per-km/#:~:text=ANWB%20heeft%20een%20berekening%20vrijgegeven,middenklas%20auto%20%E2%82%AC0%2C49.

    // https://www.rrreis.nl/nieuwe-tarieven-en-prijzen-vanaf-1-januari-2023#:~:text=De%20totaalprijs%20voor%20reizen%20op,voor%20RRReis%20wordt%20%E2%82%AC%200%2C196.
    private static final double PUBLIC_TRANSPORT_COST_KM = 0.2;
    private static final double PUBLIC_TRANSPORT_BASE_FEE = 1.08;
    private HashMap<TransportMode, Double> alphaWork = new HashMap<>();
    private HashMap<TransportMode, Double> betaTimeWork = new HashMap<>();
    private HashMap<TransportMode, Double> betaCostWork = new HashMap<>();
    private HashMap<TransportMode, Double> alphaSchool = new HashMap<>();
    private HashMap<TransportMode, Double> betaTimeSchool = new HashMap<>();
    private HashMap<TransportMode, Double> betaCostSchool = new HashMap<>();
    private HashMap<TransportMode, Double> alphaLeisure = new HashMap<>();
    private HashMap<TransportMode, Double> betaTimeLeisure = new HashMap<>();
    private HashMap<TransportMode, Double> betaCostLeisure = new HashMap<>();
    private double betaTimeWalkTransportWork;
    private double betaTimeWalkTransportSchool;
    private double betaTimeWalkTransportLeisure;
    private double betaChangesTransportWork;
    private double betaChangesTransportSchool;
    private double betaChangesTransportLeisure;

    public void setParameters(MNLparametersReader parametersReader){
        this.alphaWork = parametersReader.getAlphaWork();
        this.betaTimeWork = parametersReader.getBetaTimeWork();
        this.betaCostWork = parametersReader.getBetaCostWork();
        this.alphaSchool = parametersReader.getAlphaSchool();
        this.betaTimeSchool = parametersReader.getBetaTimeSchool();
        this.betaCostSchool = parametersReader.getBetaCostSchool();
        this.alphaLeisure = parametersReader.getAlphaLeisure();
        this.betaTimeLeisure = parametersReader.getBetaTimeLeisure();
        this.betaCostLeisure = parametersReader.getBetaCostLeisure();

        // work
        alphaWork.put(TransportMode.CAR_DRIVER, 1.0 * alphaWork.get(TransportMode.CAR_DRIVER));
        alphaWork.put(TransportMode.CAR_PASSENGER, -3.0 * alphaWork.get(TransportMode.CAR_PASSENGER));
        alphaWork.put(TransportMode.BUS_TRAM, -1.0 * alphaWork.get(TransportMode.BUS_TRAM));
        alphaWork.put(TransportMode.TRAIN, -1.0 * alphaWork.get(TransportMode.TRAIN));

        betaTimeWork.put(TransportMode.WALK, -0.04 * betaTimeWork.get(TransportMode.WALK));
        betaTimeWork.put(TransportMode.BIKE, -0.03 * betaTimeWork.get(TransportMode.BIKE));
        betaTimeWork.put(TransportMode.CAR_DRIVER, -0.02 * betaTimeWork.get(TransportMode.CAR_DRIVER));
        betaTimeWork.put(TransportMode.CAR_PASSENGER, -0.02 * betaTimeWork.get(TransportMode.CAR_PASSENGER));
        betaTimeWork.put(TransportMode.BUS_TRAM, -0.02 * betaTimeWork.get(TransportMode.BUS_TRAM));
        betaTimeWork.put(TransportMode.TRAIN, -0.02 * betaTimeWork.get(TransportMode.TRAIN));

        betaCostWork.put(TransportMode.CAR_DRIVER, 3 * betaCostWork.get(TransportMode.CAR_DRIVER));
        betaCostWork.put(TransportMode.CAR_PASSENGER, 2 * betaCostWork.get(TransportMode.CAR_PASSENGER));
        betaCostWork.put(TransportMode.BUS_TRAM, 2 * betaCostWork.get(TransportMode.BUS_TRAM));
        betaCostWork.put(TransportMode.TRAIN, 2 * betaCostWork.get(TransportMode.TRAIN));

        // school
        alphaSchool.put(TransportMode.CAR_DRIVER, 1.0 * alphaSchool.get(TransportMode.CAR_DRIVER));
        alphaSchool.put(TransportMode.CAR_PASSENGER, -3.0 * alphaSchool.get(TransportMode.CAR_PASSENGER));
        alphaSchool.put(TransportMode.BUS_TRAM, -1.0 * alphaSchool.get(TransportMode.BUS_TRAM));
        alphaSchool.put(TransportMode.TRAIN, -1.0 * alphaSchool.get(TransportMode.TRAIN));

        betaTimeSchool.put(TransportMode.WALK, -0.04 * betaTimeSchool.get(TransportMode.WALK));
        betaTimeSchool.put(TransportMode.BIKE, -0.03 * betaTimeSchool.get(TransportMode.BIKE));
        betaTimeSchool.put(TransportMode.CAR_DRIVER, -0.02 * betaTimeSchool.get(TransportMode.CAR_DRIVER));
        betaTimeSchool.put(TransportMode.CAR_PASSENGER, -0.02 * betaTimeSchool.get(TransportMode.CAR_PASSENGER));
        betaTimeSchool.put(TransportMode.BUS_TRAM, -0.02 * betaTimeSchool.get(TransportMode.BUS_TRAM));
        betaTimeSchool.put(TransportMode.TRAIN, -0.02 * betaTimeSchool.get(TransportMode.TRAIN));

        betaCostSchool.put(TransportMode.CAR_DRIVER, 3 * betaCostSchool.get(TransportMode.CAR_DRIVER));
        betaCostSchool.put(TransportMode.CAR_PASSENGER, 2 * betaCostSchool.get(TransportMode.CAR_PASSENGER));
        betaCostSchool.put(TransportMode.BUS_TRAM, 2 * betaCostSchool.get(TransportMode.BUS_TRAM));
        betaCostSchool.put(TransportMode.TRAIN, 2 * betaCostSchool.get(TransportMode.TRAIN));

        // leisure
        alphaLeisure.put(TransportMode.CAR_DRIVER, 1.0 * alphaLeisure.get(TransportMode.CAR_DRIVER));
        alphaLeisure.put(TransportMode.CAR_PASSENGER, -1.0 * alphaLeisure.get(TransportMode.CAR_PASSENGER));
        alphaLeisure.put(TransportMode.BUS_TRAM, -1.0 * alphaLeisure.get(TransportMode.BUS_TRAM));
        alphaLeisure.put(TransportMode.TRAIN, -1.0 * alphaLeisure.get(TransportMode.TRAIN));

        betaTimeLeisure.put(TransportMode.WALK, -0.03 * betaTimeLeisure.get(TransportMode.WALK));
        betaTimeLeisure.put(TransportMode.BIKE, -0.02 * betaTimeLeisure.get(TransportMode.BIKE));
        betaTimeLeisure.put(TransportMode.CAR_DRIVER, -0.018 * betaTimeLeisure.get(TransportMode.CAR_DRIVER));
        betaTimeLeisure.put(TransportMode.CAR_PASSENGER, -0.018 * betaTimeLeisure.get(TransportMode.CAR_PASSENGER));
        betaTimeLeisure.put(TransportMode.BUS_TRAM, -0.018 * betaTimeLeisure.get(TransportMode.BUS_TRAM));
        betaTimeLeisure.put(TransportMode.TRAIN, -0.018 * betaTimeLeisure.get(TransportMode.TRAIN));

        betaCostLeisure.put(TransportMode.CAR_DRIVER, -0.18 * betaCostLeisure.get(TransportMode.CAR_DRIVER));
        betaCostLeisure.put(TransportMode.CAR_PASSENGER, -0.18 * betaCostLeisure.get(TransportMode.CAR_PASSENGER));
        betaCostLeisure.put(TransportMode.BUS_TRAM, -0.12 * betaCostLeisure.get(TransportMode.BUS_TRAM));
        betaCostLeisure.put(TransportMode.TRAIN, -0.12 * betaCostLeisure.get(TransportMode.TRAIN));

        // non-map values
        double BETA_WALKTIME_TRANSPORT = -0.03;
        this.betaTimeWalkTransportWork = BETA_WALKTIME_TRANSPORT * parametersReader.getBetaTimeWalkTransportWork();
        this.betaTimeWalkTransportSchool = BETA_WALKTIME_TRANSPORT * parametersReader.getBetaTimeWalkTransportSchool();
        this.betaTimeWalkTransportLeisure = BETA_WALKTIME_TRANSPORT * parametersReader.getBetaTimeWalkTransportLeisure();

        double BETA_CHANGES_TRANSPORT = -0.3;
        this.betaChangesTransportWork = BETA_CHANGES_TRANSPORT * parametersReader.getBetaChangesTransportWork();
        this.betaChangesTransportSchool = BETA_CHANGES_TRANSPORT * parametersReader.getBetaChangesTransportSchool();
        this.betaChangesTransportLeisure = BETA_CHANGES_TRANSPORT * parametersReader.getBetaChangesTransportLeisure();

    }

    public HashMap<TransportMode, Double> getChoiceProbabilities (
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
        HashMap<TransportMode, Double> alpha = null;
        HashMap<TransportMode, Double> betaTime = null;
        HashMap<TransportMode, Double> betaCost = null;
        double betaTimeWalkTransport;
        double betaChangesTransport;

        if (departureType.equals(ActivityType.WORK) | arrivalType.equals(ActivityType.WORK)) {
            alpha = alphaWork;
            betaTime = betaTimeWork;
            betaCost = betaCostWork;
            betaTimeWalkTransport = betaTimeWalkTransportWork;
            betaChangesTransport = betaChangesTransportWork;
        } else if (departureType.equals(ActivityType.SCHOOL) | arrivalType.equals(ActivityType.SCHOOL)) {
            alpha = alphaSchool;
            betaTime = betaTimeSchool;
            betaCost = betaCostSchool;
            betaTimeWalkTransport = betaTimeWalkTransportSchool;
            betaChangesTransport = betaChangesTransportSchool;
        } else {
            alpha = alphaLeisure;
            betaTime = betaTimeLeisure;
            betaCost = betaCostLeisure;
            betaTimeWalkTransport = betaTimeWalkTransportLeisure;
            betaChangesTransport = betaChangesTransportLeisure;
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
                    utility_i = betaTime.get(TransportMode.WALK) * travelTimes.get(TransportMode.WALK);
                    break;
                case BIKE:
                    utility_i = betaTime.get(TransportMode.BIKE) * travelTimes.get(TransportMode.BIKE);
                    break;
                case CAR_DRIVER:
                    utility_i = alpha.get(TransportMode.CAR_DRIVER) + betaTime.get(TransportMode.CAR_DRIVER) * travelTimes.get(TransportMode.CAR_DRIVER) + betaCost.get(transportMode) * getCarCost(travelDistances.get(TransportMode.CAR_DRIVER));
                    break;
                case CAR_PASSENGER:
                    utility_i = alpha.get(TransportMode.CAR_PASSENGER) + betaTime.get(TransportMode.CAR_PASSENGER) * travelTimes.get(TransportMode.CAR_PASSENGER) + betaCost.get(transportMode) * getCarCost(travelDistances.get(TransportMode.CAR_PASSENGER));
                    break;
                case BUS_TRAM:
                    utility_i = alpha.get(TransportMode.BUS_TRAM) +
                            betaTime.get(TransportMode.BUS_TRAM) * travelTimes.get(TransportMode.BUS_TRAM) +
                            betaCost.get(TransportMode.BUS_TRAM) * getPublicTransportCost(travelDistances.get(TransportMode.BUS_TRAM)) +
                            betaTimeWalkTransport * walkTimeBus +
                            betaChangesTransport * nChangesBus;
                    break;
                case TRAIN:
                    utility_i = alpha.get(TransportMode.TRAIN) +
                            betaTime.get(TransportMode.TRAIN) * travelTimes.get(TransportMode.TRAIN) +
                            betaCost.get(TransportMode.TRAIN) * getPublicTransportCost(travelDistances.get(TransportMode.TRAIN)) +
                            betaTime.get(TransportMode.BUS_TRAM) * busTimeTrain +
                            betaCost.get(TransportMode.BUS_TRAM) * getPublicTransportCost(busDistanceTrain) +
                            betaTimeWalkTransport * walkTimeTrain +
                            betaChangesTransport * nChangesTrain;
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
