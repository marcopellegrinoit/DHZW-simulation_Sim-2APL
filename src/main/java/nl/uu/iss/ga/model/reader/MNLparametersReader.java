package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MNLparametersReader implements Context  {

    CSVReader reader;
    private static final Logger LOGGER = Logger.getLogger(MNLparametersReader.class.getName());

    private final HashMap<TransportMode, Double> alpha;
    private final HashMap<TransportMode, Double> betaTime;
    private final HashMap<TransportMode, Double> betaCost;
    private double betaTimeWalkTransport;
    private double betaChangesTransport;

    public MNLparametersReader(File parameterFile, int parameterSetIndex) {
        this.alpha = new HashMap<>();
        this.betaTime = new HashMap<>();
        this.betaCost = new HashMap<>();

        this.readParameters(parameterFile, parameterSetIndex);
    }

    private void readParameters(File routingFile, int parameterSetIndex) {
        LOGGER.log(Level.INFO, "Reading parameters MNL file " + routingFile.toString());

        try {
            this.reader = new CSVReader(new FileReader(routingFile));

            // Skip the first line of the CSV file (the header)
            reader.skip(parameterSetIndex);

            String [] line = reader.readNext();

            alpha.put(TransportMode.WALK, Double.parseDouble(line[0]));
            alpha.put(TransportMode.BIKE, Double.parseDouble(line[1]));
            alpha.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[2]));
            alpha.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[3]));
            alpha.put(TransportMode.BUS_TRAM, Double.parseDouble(line[4]));
            alpha.put(TransportMode.TRAIN, Double.parseDouble(line[5]));

            betaTime.put(TransportMode.WALK, Double.parseDouble(line[5]));
            betaTime.put(TransportMode.BIKE, Double.parseDouble(line[6]));
            betaTime.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[7]));
            betaTime.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[8]));
            betaTime.put(TransportMode.BUS_TRAM, Double.parseDouble(line[9]));
            betaTime.put(TransportMode.TRAIN, Double.parseDouble(line[10]));

            betaCost.put(TransportMode.CAR_DRIVER, Double.parseDouble(line[11]));
            betaCost.put(TransportMode.CAR_PASSENGER, Double.parseDouble(line[12]));
            betaCost.put(TransportMode.BUS_TRAM, Double.parseDouble(line[13]));
            betaCost.put(TransportMode.TRAIN, Double.parseDouble(line[14]));

            betaTimeWalkTransport = Double.parseDouble(line[15]);
            betaChangesTransport = Double.parseDouble(line[16]);

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<TransportMode, Double> getAlpha(){
        return this.alpha;
    }
    public HashMap<TransportMode, Double> getBetaTime(){
        return this.betaTime;
    }
    public HashMap<TransportMode, Double> getBetaCost(){
        return this.betaCost;
    }

    public double getBetaTimeWalkTransport(){
        return this.betaChangesTransport;
    }

    public double getBetaChangesTransport(){
        return this.betaChangesTransport;
    }

}
