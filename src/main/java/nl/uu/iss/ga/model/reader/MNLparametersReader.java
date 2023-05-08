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

    public MNLparametersReader(File parameterDir, int parameterSetIndex) {
        this.alphaWork = new HashMap<>();
        this.betaTimeWork = new HashMap<>();
        this.betaCostWork = new HashMap<>();
        this.alphaSchool = new HashMap<>();
        this.betaTimeSchool = new HashMap<>();
        this.betaCostSchool = new HashMap<>();
        this.alphaLeisure = new HashMap<>();
        this.betaTimeLeisure = new HashMap<>();
        this.betaCostLeisure = new HashMap<>();

        readParameters(new File(parameterDir, "parametersets.csv"), parameterSetIndex);
    }

    private void readParameters(File routingFile, int parameterSetIndex) {
        LOGGER.log(Level.INFO, "Reading parameters MNL file " + routingFile.toString());

        try {
            this.reader = new CSVReader(new FileReader(routingFile));

            // Skip the first line of the CSV file (the header)
            reader.skip(parameterSetIndex);

            String [] line = reader.readNext();

            // work
            alphaWork.put(TransportMode.WALK, Double.parseDouble(line[0]));
            alphaWork.put(TransportMode.BIKE, Double.parseDouble(line[1]));
            alphaWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[2]));
            alphaWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[3]));
            alphaWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[4]));
            alphaWork.put(TransportMode.TRAIN, Double.parseDouble(line[5]));

            betaTimeWork.put(TransportMode.WALK, Double.parseDouble(line[6]));
            betaTimeWork.put(TransportMode.BIKE, Double.parseDouble(line[7]));
            betaTimeWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[8]));
            betaTimeWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[9]));
            betaTimeWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[10]));
            betaTimeWork.put(TransportMode.TRAIN, Double.parseDouble(line[11]));

            betaCostWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[12]));
            betaCostWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[13]));
            betaCostWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[14]));
            betaCostWork.put(TransportMode.TRAIN, Double.parseDouble(line[15]));

            // school
            alphaSchool.put(TransportMode.WALK, Double.parseDouble(line[16]));
            alphaSchool.put(TransportMode.BIKE, Double.parseDouble(line[17]));
            alphaSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[18]));
            alphaSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[19]));
            alphaSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[20]));
            alphaSchool.put(TransportMode.TRAIN, Double.parseDouble(line[21]));

            betaTimeSchool.put(TransportMode.WALK, Double.parseDouble(line[22]));
            betaTimeSchool.put(TransportMode.BIKE, Double.parseDouble(line[23]));
            betaTimeSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[24]));
            betaTimeSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[25]));
            betaTimeSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[26]));
            betaTimeSchool.put(TransportMode.TRAIN, Double.parseDouble(line[27]));

            betaCostSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[28]));
            betaCostSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[29]));
            betaCostSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[30]));
            betaCostSchool.put(TransportMode.TRAIN, Double.parseDouble(line[31]));

            // other: leisure
            alphaLeisure.put(TransportMode.WALK, Double.parseDouble(line[32]));
            alphaLeisure.put(TransportMode.BIKE, Double.parseDouble(line[33]));
            alphaLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[34]));
            alphaLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[35]));
            alphaLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[36]));
            alphaLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[37]));

            betaTimeLeisure.put(TransportMode.WALK, Double.parseDouble(line[38]));
            betaTimeLeisure.put(TransportMode.BIKE, Double.parseDouble(line[39]));
            betaTimeLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[40]));
            betaTimeLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[41]));
            betaTimeLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[42]));
            betaTimeLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[43]));

            betaCostLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[44]));
            betaCostLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[45]));
            betaCostLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[46]));
            betaCostLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[47]));

            betaTimeWalkBus = Double.parseDouble(line[48]);
            betaTimeWalkTrain = Double.parseDouble(line[49]);
            betaChangesBus = Double.parseDouble(line[50]);
            betaChangesTrain = Double.parseDouble(line[51]);
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
