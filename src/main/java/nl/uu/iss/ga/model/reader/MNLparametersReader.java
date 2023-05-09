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
    private double betaTimeWalkTransportWork;
    private double betaTimeWalkTransportSchool;
    private double betaTimeWalkTransportLeisure;
    private double betaChangesTransportWork;
    private double betaChangesTransportSchool;
    private double betaChangesTransportLeisure;

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
            alphaWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[0]));
            alphaWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[1]));
            alphaWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[2]));
            alphaWork.put(TransportMode.TRAIN, Double.parseDouble(line[3]));

            betaTimeWork.put(TransportMode.WALK, Double.parseDouble(line[4]));
            betaTimeWork.put(TransportMode.BIKE, Double.parseDouble(line[5]));
            betaTimeWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[6]));
            betaTimeWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[7]));
            betaTimeWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[8]));
            betaTimeWork.put(TransportMode.TRAIN, Double.parseDouble(line[9]));

            betaCostWork.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[10]));
            betaCostWork.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[11]));
            betaCostWork.put(TransportMode.BUS_TRAM, Double.parseDouble(line[12]));
            betaCostWork.put(TransportMode.TRAIN, Double.parseDouble(line[13]));

            // school
            alphaSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[14]));
            alphaSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[15]));
            alphaSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[16]));
            alphaSchool.put(TransportMode.TRAIN, Double.parseDouble(line[17]));

            betaTimeSchool.put(TransportMode.WALK, Double.parseDouble(line[18]));
            betaTimeSchool.put(TransportMode.BIKE, Double.parseDouble(line[19]));
            betaTimeSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[20]));
            betaTimeSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[21]));
            betaTimeSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[22]));
            betaTimeSchool.put(TransportMode.TRAIN, Double.parseDouble(line[23]));

            betaCostSchool.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[24]));
            betaCostSchool.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[25]));
            betaCostSchool.put(TransportMode.BUS_TRAM, Double.parseDouble(line[26]));
            betaCostSchool.put(TransportMode.TRAIN, Double.parseDouble(line[27]));

            // other: leisure
            alphaLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[28]));
            alphaLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[29]));
            alphaLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[30]));
            alphaLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[31]));

            betaTimeLeisure.put(TransportMode.WALK, Double.parseDouble(line[32]));
            betaTimeLeisure.put(TransportMode.BIKE, Double.parseDouble(line[33]));
            betaTimeLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[34]));
            betaTimeLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[35]));
            betaTimeLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[36]));
            betaTimeLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[37]));

            betaCostLeisure.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[38]));
            betaCostLeisure.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[39]));
            betaCostLeisure.put(TransportMode.BUS_TRAM, Double.parseDouble(line[40]));
            betaCostLeisure.put(TransportMode.TRAIN, Double.parseDouble(line[41]));

            betaTimeWalkTransportWork = Double.parseDouble(line[42]);
            betaTimeWalkTransportSchool = Double.parseDouble(line[43]);
            betaTimeWalkTransportLeisure = Double.parseDouble(line[44]);

            betaChangesTransportWork = Double.parseDouble(line[45]);
            betaChangesTransportSchool = Double.parseDouble(line[46]);
            betaChangesTransportLeisure = Double.parseDouble(line[47]);

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

    public double getBetaTimeWalkTransportWork(){
        return this.betaChangesTransportWork;
    }
    public double getBetaTimeWalkTransportSchool(){
        return this.betaChangesTransportSchool;
    }
    public double getBetaTimeWalkTransportLeisure(){
        return this.betaChangesTransportLeisure;
    }
    public double getBetaChangesTransportWork(){
        return this.betaChangesTransportWork;
    }
    public double getBetaChangesTransportSchool(){
        return this.betaChangesTransportSchool;
    }
    public double getBetaChangesTransportLeisure(){
        return this.betaChangesTransportLeisure;
    }

}
