package main.java.nl.uu.iss.ga.util;

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
    private HashMap<TransportMode, Double> alpha = new HashMap<>();
    private HashMap<TransportMode, Double> betaTime = new HashMap<>();
    private HashMap<TransportMode, Double> betaCost = new HashMap<>();

    private double betaTimeWalkTransport;
    private double betaChangesTransport;

    public void setParameters(MNLparametersReader parametersReader){
        this.alpha = parametersReader.getAlpha();
        this.betaTime = parametersReader.getBetaTime();
        this.betaCost = parametersReader.getBetaCost();

        this.betaTimeWalkTransport = parametersReader.getBetaTimeWalkTransport();
        this.betaChangesTransport = parametersReader.getBetaChangesTransport();
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
            int nChangesTrain) {

        // select the coefficients based on the activity type

        // probability distribution of transport modes
        HashMap<TransportMode, Double> choiceProbabilities = new HashMap<>();

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

            choiceProbabilities.put(transportMode, utility_i);  // save utility
        }

        // exponential of each utility
        double sumUtilitiesExp = 0.0;
        for (TransportMode transportMode: choiceProbabilities.keySet()){
            choiceProbabilities.put(transportMode, Math.exp(choiceProbabilities.get(transportMode)));
            sumUtilitiesExp += choiceProbabilities.get(transportMode);
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
