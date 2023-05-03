package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

public class RoutingSimmetricReader {

    private static final Logger LOGGER = Logger.getLogger(RoutingSimmetricReader.class.getName());
    private final HashMap<TwoStringKeys, Double> travelTimes;
    private final HashMap<TwoStringKeys, Double> distances;

    public RoutingSimmetricReader(List<File> routingFile) {
        this.travelTimes = new HashMap<>();
        this.distances = new HashMap<>();
        for(File f : routingFile) {
            readTravelTimes(f);
        }
    }

    public HashMap<TwoStringKeys, Double> getAllTravelTimes() {
        return this.travelTimes;
    }

    public HashMap<TwoStringKeys, Double> getAllDistances() {
        return this.distances;
    }

    public Double getTravelTime(String location1, String location2) {
        TwoStringKeys key = new TwoStringKeys(location1, location2);
        return travelTimes.get(key);
    }
    public Double getTravelTime(TwoStringKeys key) {
        return travelTimes.get(key);
    }

    public Double getDistance(String location1, String location2) {
        TwoStringKeys key = new TwoStringKeys(location1, location2);
        return distances.get(key);
    }

    public Double getDistance(TwoStringKeys key) {
        return distances.get(key);
    }

    private void readTravelTimes(File routingFile) {
        LOGGER.log(Level.INFO, "Reading routing walk, bike, car file " + routingFile.toString());

        try {
            CSVReader reader = new CSVReader(new FileReader(routingFile));
            String[] line;
            // Skip the first line of the CSV file (the header)
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // Parse the ID combination and value from the CSV line
                String[] ids = line[0].split(",");
                String id1 = line[0].trim();
                String id2 = line[1].trim();
                double travelTime = Double.parseDouble(line[6].trim());
                double distance = Double.parseDouble(line[7].trim());

                // Create a TwoStringKeys object for the ID combination
                TwoStringKeys key = new TwoStringKeys(id1, id2);

                // Add the ID combination and value to the HashMap
                travelTimes.put(key, travelTime);
                distances.put(key, distance);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
