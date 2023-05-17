package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MNLparametersReader implements Context  {

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

    public MNLparametersReader(File parameterFile, int parameterSetIndex) {
        this.alphaWork = new HashMap<>();
        this.betaTimeWork = new HashMap<>();
        this.betaCostWork = new HashMap<>();
        this.alphaSchool = new HashMap<>();
        this.betaTimeSchool = new HashMap<>();
        this.betaCostSchool = new HashMap<>();
        this.alphaLeisure = new HashMap<>();
        this.betaTimeLeisure = new HashMap<>();
        this.betaCostLeisure = new HashMap<>();

        this.readParameters(parameterFile, parameterSetIndex);

        this.addLiteratureCoefficients();
    }

    private void addLiteratureCoefficients() {
        this.alphaWork.put(TransportMode.CAR_DRIVER, 1.0 * this.alphaWork.get(TransportMode.CAR_DRIVER));
        this.alphaWork.put(TransportMode.CAR_PASSENGER, -3.0 * this.alphaWork.get(TransportMode.CAR_PASSENGER));
        this.alphaWork.put(TransportMode.BUS_TRAM, -1.0 * this.alphaWork.get(TransportMode.BUS_TRAM));
        this.alphaWork.put(TransportMode.TRAIN, -1.0 * this.alphaWork.get(TransportMode.TRAIN));

        this.betaTimeWork.put(TransportMode.WALK, -0.04 * this.betaTimeWork.get(TransportMode.WALK));
        this.betaTimeWork.put(TransportMode.BIKE, -0.03 * this.betaTimeWork.get(TransportMode.BIKE));
        this.betaTimeWork.put(TransportMode.CAR_DRIVER, -0.02 * this.betaTimeWork.get(TransportMode.CAR_DRIVER));
        this.betaTimeWork.put(TransportMode.CAR_PASSENGER, -0.02 * this.betaTimeWork.get(TransportMode.CAR_PASSENGER));
        this.betaTimeWork.put(TransportMode.BUS_TRAM, -0.02 * this.betaTimeWork.get(TransportMode.BUS_TRAM));
        this.betaTimeWork.put(TransportMode.TRAIN, -0.02 * this.betaTimeWork.get(TransportMode.TRAIN));

        this.betaCostWork.put(TransportMode.CAR_DRIVER, -0.15 * this.betaCostWork.get(TransportMode.CAR_DRIVER));
        this.betaCostWork.put(TransportMode.CAR_PASSENGER, -0.15 * this.betaCostWork.get(TransportMode.CAR_PASSENGER));
        this.betaCostWork.put(TransportMode.BUS_TRAM, -0.1 * this.betaCostWork.get(TransportMode.BUS_TRAM));
        this.betaCostWork.put(TransportMode.TRAIN, -0.1 * this.betaCostWork.get(TransportMode.TRAIN));

        // school
        this.alphaSchool.put(TransportMode.CAR_DRIVER, 1.0 * this.alphaSchool.get(TransportMode.CAR_DRIVER));
        this.alphaSchool.put(TransportMode.CAR_PASSENGER, -3.0 * this.alphaSchool.get(TransportMode.CAR_PASSENGER));
        this.alphaSchool.put(TransportMode.BUS_TRAM, -1.0 * this.alphaSchool.get(TransportMode.BUS_TRAM));
        this.alphaSchool.put(TransportMode.TRAIN, -1.0 * this.alphaSchool.get(TransportMode.TRAIN));

        this.betaTimeSchool.put(TransportMode.WALK, -0.04 * this.betaTimeSchool.get(TransportMode.WALK));
        this.betaTimeSchool.put(TransportMode.BIKE, -0.03 * this.betaTimeSchool.get(TransportMode.BIKE));
        this.betaTimeSchool.put(TransportMode.CAR_DRIVER, -0.02 * this.betaTimeSchool.get(TransportMode.CAR_DRIVER));
        this.betaTimeSchool.put(TransportMode.CAR_PASSENGER, -0.02 * this.betaTimeSchool.get(TransportMode.CAR_PASSENGER));
        this.betaTimeSchool.put(TransportMode.BUS_TRAM, -0.02 * this.betaTimeSchool.get(TransportMode.BUS_TRAM));
        this.betaTimeSchool.put(TransportMode.TRAIN, -0.02 * this.betaTimeSchool.get(TransportMode.TRAIN));

        this.betaCostSchool.put(TransportMode.CAR_DRIVER, -0.15 * this.betaCostSchool.get(TransportMode.CAR_DRIVER));
        this.betaCostSchool.put(TransportMode.CAR_PASSENGER, -0.15 * this.betaCostSchool.get(TransportMode.CAR_PASSENGER));
        this.betaCostSchool.put(TransportMode.BUS_TRAM, -0.1 * this.betaCostSchool.get(TransportMode.BUS_TRAM));
        this.betaCostSchool.put(TransportMode.TRAIN, -0.1 * this.betaCostSchool.get(TransportMode.TRAIN));

        // leisure
        this.alphaLeisure.put(TransportMode.CAR_DRIVER, 1.0 * this.alphaLeisure.get(TransportMode.CAR_DRIVER));
        this.alphaLeisure.put(TransportMode.CAR_PASSENGER, -1.0 * this.alphaLeisure.get(TransportMode.CAR_PASSENGER));
        this.alphaLeisure.put(TransportMode.BUS_TRAM, -1.0 * this.alphaLeisure.get(TransportMode.BUS_TRAM));
        this. alphaLeisure.put(TransportMode.TRAIN, -1.0 * this.alphaLeisure.get(TransportMode.TRAIN));

        this.betaTimeLeisure.put(TransportMode.WALK, -0.03 * this.betaTimeLeisure.get(TransportMode.WALK));
        this.betaTimeLeisure.put(TransportMode.BIKE, -0.02 * this.betaTimeLeisure.get(TransportMode.BIKE));
        this.betaTimeLeisure.put(TransportMode.CAR_DRIVER, -0.018 * this.betaTimeLeisure.get(TransportMode.CAR_DRIVER));
        this.betaTimeLeisure.put(TransportMode.CAR_PASSENGER, -0.018 * this.betaTimeLeisure.get(TransportMode.CAR_PASSENGER));
        this.betaTimeLeisure.put(TransportMode.BUS_TRAM, -0.018 * this.betaTimeLeisure.get(TransportMode.BUS_TRAM));
        this.betaTimeLeisure.put(TransportMode.TRAIN, -0.018 * this.betaTimeLeisure.get(TransportMode.TRAIN));

        this.betaCostLeisure.put(TransportMode.CAR_DRIVER, -0.18 * this.betaCostLeisure.get(TransportMode.CAR_DRIVER));
        this.betaCostLeisure.put(TransportMode.CAR_PASSENGER, -0.18 * this.betaCostLeisure.get(TransportMode.CAR_PASSENGER));
        this.betaCostLeisure.put(TransportMode.BUS_TRAM, -0.12 * this.betaCostLeisure.get(TransportMode.BUS_TRAM));
        this.betaCostLeisure.put(TransportMode.TRAIN, -0.12 * this.betaCostLeisure.get(TransportMode.TRAIN));

        // non-map values
        double BETA_WALKTIME_TRANSPORT = -0.03;
        this.betaTimeWalkTransportWork = BETA_WALKTIME_TRANSPORT * this.betaTimeWalkTransportWork;
        this.betaTimeWalkTransportSchool = BETA_WALKTIME_TRANSPORT * this.betaTimeWalkTransportSchool;
        this.betaTimeWalkTransportLeisure = BETA_WALKTIME_TRANSPORT * this.betaTimeWalkTransportLeisure;

        double BETA_CHANGES_TRANSPORT = -0.3;
        this.betaChangesTransportWork = BETA_CHANGES_TRANSPORT * this.betaChangesTransportWork;
        this.betaChangesTransportSchool = BETA_CHANGES_TRANSPORT * this.betaChangesTransportSchool;
        this.betaChangesTransportLeisure = BETA_CHANGES_TRANSPORT * this.betaChangesTransportLeisure;
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
