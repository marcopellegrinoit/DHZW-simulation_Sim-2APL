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
    private double betaTimeWalkBus;
    private double betaTimeWalkTrain;
    private double betaChangesBus;
    private double betaChangesTrain;

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

        this.betaTimeWalkBus = parametersReader.getBetaTimeWalkBus();
        this.betaTimeWalkTrain = parametersReader.getBetaTimeWalkTrain();
        this.betaChangesBus = parametersReader.getBetaChangesBus();
        this.betaChangesTrain = parametersReader.getBetaChangesTrain();
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
