package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MNLparametersReader {

    CSVReader reader;
    private static final Logger LOGGER = Logger.getLogger(MNLparametersReader.class.getName());

    private final HashMap<TransportMode, Double> alphaWork;
    private final HashMap<TransportMode, Double> betaTimeWork;
    private final HashMap<TransportMode, Double> betaCostWork;
    private final HashMap<TransportMode, Double> alphaSchool;
    private final HashMap<TransportMode, Double> betaTimeSchool;
    private final HashMap<TransportMode, Double> betaCostSchool;
    private final HashMap<TransportMode, Double> alphaLeisure;
    private final HashMap<TransportMode, Double> betaTimeLeisure;
    private final HashMap<TransportMode, Double> betaCostLeisure;
    private double betaTimeWalkBus;
    private double betaTimeWalkTrain ;
    private double betaChangesBus;
    private double betaChangesTrain;

    public MNLparametersReader(File parameterDir) {
        this.alphaWork = new HashMap<>();
        this.betaTimeWork = new HashMap<>();
        this.betaCostWork = new HashMap<>();
        this.alphaSchool = new HashMap<>();
        this.betaTimeSchool = new HashMap<>();
        this.betaCostSchool = new HashMap<>();
        this.alphaLeisure = new HashMap<>();
        this.betaTimeLeisure = new HashMap<>();
        this.betaCostLeisure = new HashMap<>();

        readParameters(new File(parameterDir, "parameterset.csv"));
    }

    private double getNext() throws CsvValidationException, IOException {
        String [] line = reader.readNext();
        return Double.parseDouble(line[1].trim());
    }

    private void readParameters(File routingFile) {
        LOGGER.log(Level.INFO, "Reading parameters MNL file " + routingFile.toString());

        try {
            this.reader = new CSVReader(new FileReader(routingFile));

            // Skip the first line of the CSV file (the header)
            reader.skip(1);

            // work
            alphaWork.put(TransportMode.WALK, this.getNext());
            alphaWork.put(TransportMode.BIKE, this.getNext());
            alphaWork.put(TransportMode.CAR_DRIVER, this.getNext());
            alphaWork.put(TransportMode.CAR_PASSENGER, this.getNext());
            alphaWork.put(TransportMode.BUS_TRAM, this.getNext());
            alphaWork.put(TransportMode.TRAIN, this.getNext());

            betaTimeWork.put(TransportMode.WALK, this.getNext());
            betaTimeWork.put(TransportMode.BIKE, this.getNext());
            betaTimeWork.put(TransportMode.CAR_DRIVER, this.getNext());
            betaTimeWork.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaTimeWork.put(TransportMode.BUS_TRAM, this.getNext());
            betaTimeWork.put(TransportMode.TRAIN, this.getNext());

            betaCostWork.put(TransportMode.CAR_DRIVER, this.getNext());
            betaCostWork.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaCostWork.put(TransportMode.BUS_TRAM, this.getNext());
            betaCostWork.put(TransportMode.TRAIN, this.getNext());

            // school
            alphaSchool.put(TransportMode.WALK, this.getNext());
            alphaSchool.put(TransportMode.BIKE, this.getNext());
            alphaSchool.put(TransportMode.CAR_DRIVER, this.getNext());
            alphaSchool.put(TransportMode.CAR_PASSENGER, this.getNext());
            alphaSchool.put(TransportMode.BUS_TRAM, this.getNext());
            alphaSchool.put(TransportMode.TRAIN, this.getNext());

            betaTimeSchool.put(TransportMode.WALK, this.getNext());
            betaTimeSchool.put(TransportMode.BIKE, this.getNext());
            betaTimeSchool.put(TransportMode.CAR_DRIVER, this.getNext());
            betaTimeSchool.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaTimeSchool.put(TransportMode.BUS_TRAM, this.getNext());
            betaTimeSchool.put(TransportMode.TRAIN, this.getNext());

            betaCostSchool.put(TransportMode.CAR_DRIVER, this.getNext());
            betaCostSchool.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaCostSchool.put(TransportMode.BUS_TRAM, this.getNext());
            betaCostSchool.put(TransportMode.TRAIN, this.getNext());

            // other: leisure
            alphaLeisure.put(TransportMode.WALK, this.getNext());
            alphaLeisure.put(TransportMode.BIKE, this.getNext());
            alphaLeisure.put(TransportMode.CAR_DRIVER, this.getNext());
            alphaLeisure.put(TransportMode.CAR_PASSENGER, this.getNext());
            alphaLeisure.put(TransportMode.BUS_TRAM, this.getNext());
            alphaLeisure.put(TransportMode.TRAIN, this.getNext());

            betaTimeLeisure.put(TransportMode.WALK, this.getNext());
            betaTimeLeisure.put(TransportMode.BIKE, this.getNext());
            betaTimeLeisure.put(TransportMode.CAR_DRIVER, this.getNext());
            betaTimeLeisure.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaTimeLeisure.put(TransportMode.BUS_TRAM, this.getNext());
            betaTimeLeisure.put(TransportMode.TRAIN, -this.getNext());

            betaCostLeisure.put(TransportMode.CAR_DRIVER, this.getNext());
            betaCostLeisure.put(TransportMode.CAR_PASSENGER, this.getNext());
            betaCostLeisure.put(TransportMode.BUS_TRAM, this.getNext());
            betaCostLeisure.put(TransportMode.TRAIN, this.getNext());

            betaTimeWalkBus = this.getNext();
            betaTimeWalkTrain = this.getNext();
            betaChangesBus = this.getNext();
            betaChangesTrain = this.getNext();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<TransportMode, Double> getAlphaWork(){
        return this.alphaWork;
    }
    public HashMap<TransportMode, Double> getBetaTimeWork(){
        return this.betaTimeWork;
    }
    public HashMap<TransportMode, Double> getBetaCostWork(){
        return this.betaCostWork;
    }
    public HashMap<TransportMode, Double> getAlphaSchool(){
        return this.alphaSchool;
    }
    public HashMap<TransportMode, Double> getBetaTimeSchool(){
        return this.betaTimeSchool;
    }
    public HashMap<TransportMode, Double> getBetaCostSchool(){
        return this.betaCostSchool;
    }
    public HashMap<TransportMode, Double> getAlphaLeisure(){
        return this.alphaLeisure;
    }
    public HashMap<TransportMode, Double> getBetaTimeLeisure(){
        return this.betaTimeLeisure;
    }
    public HashMap<TransportMode, Double> getBetaCostLeisure(){
        return this.betaCostLeisure;
    }

    public double getBetaTimeWalkBus(){
        return this.betaTimeWalkBus;
    }
    public double getBetaTimeWalkTrain(){
        return this.betaTimeWalkTrain;
    }
    public double getBetaChangesBus(){
        return this.betaChangesBus;
    }
    public double getBetaChangesTrain(){
        return this.betaChangesTrain;
    }

}
