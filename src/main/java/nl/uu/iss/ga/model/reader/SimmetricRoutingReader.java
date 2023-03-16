package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;

public class SimmetricRoutingReader {

    private static final Logger LOGGER = Logger.getLogger(SimmetricRoutingReader.class.getName());
    private final HashMap<TwoStringKeys, Double> travelTimes;
    private final HashMap<TwoStringKeys, Double> distances;


    public SimmetricRoutingReader(List<File> routingFile) {
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

    public Double getDistance(String location1, String location2) {
        TwoStringKeys key = new TwoStringKeys(location1, location2);
        return distances.get(key);
    }

    public HashMap<TwoStringKeys, Double> getTravelTimesFrom(List<String> locations) {
        return travelTimes.entrySet().stream()
                .filter(entry -> locations.contains(entry.getKey().getKey1()) || locations.contains(entry.getKey().getKey2()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, HashMap::new));
    }

    public HashMap<TwoStringKeys, Double> getDistancesFrom(List<String> locations) {
        return distances.entrySet().stream()
                .filter(entry -> locations.contains(entry.getKey().getKey1()) || locations.contains(entry.getKey().getKey2()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, HashMap::new));
    }

    private void readTravelTimes(File routingFile) {
        LOGGER.log(Level.INFO, "Reading travel time file " + routingFile.toString());

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
