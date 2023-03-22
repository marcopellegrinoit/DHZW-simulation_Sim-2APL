package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.TwoStringKeys;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsimmetricRoutingReader {

    private static final Logger LOGGER = Logger.getLogger(AsimmetricRoutingReader.class.getName());
    private final HashMap<String, HashMap<String, Double>> travelTimes;
    private final HashMap<String, HashMap<String, Double>> distances;


    public AsimmetricRoutingReader(List<File> routingFile) {
        this.travelTimes = new HashMap<>();
        this.distances = new HashMap<>();
        for(File f : routingFile) {
            readTravelTimes(f);
        }
    }

    public HashMap<String, HashMap<String, Double>> getAllTravelTimes() {
        return this.travelTimes;
    }

    public HashMap<String, HashMap<String, Double>> getAllDistances() {
        return this.distances;
    }

    public Double getTravelTime(String location1, String location2) {
        return this.travelTimes.get(location1).get(location2);
    }


    public Double getDistance(String location1, String location2) {
        return this.distances.get(location1).get(location2);
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

                // Add to the HashMaps
                this.travelTimes.get(id1).put(id2, travelTime);
                this.distances.get(id1).put(id2, travelTime);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
