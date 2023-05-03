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

public class BeelineDistanceReader {

    private static final Logger LOGGER = Logger.getLogger(BeelineDistanceReader.class.getName());
    private final HashMap<TwoStringKeys, Double> distances;

    public BeelineDistanceReader(List<File> routingFile) {
        this.distances = new HashMap<>();
        for(File f : routingFile) {
            readBeelineDistances(f);
        }
    }

    public HashMap<TwoStringKeys, Double> getAllDistances() {
        return this.distances;
    }

    public Double getDistance(String location1, String location2) {
        TwoStringKeys key = new TwoStringKeys(location1, location2);
        return distances.get(key);
    }

    public Double getDistance(TwoStringKeys key) {
        return distances.get(key);
    }

    private void readBeelineDistances(File routingFile) {
        LOGGER.log(Level.INFO, "Reading beeline distance file " + routingFile.toString());

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
                double distance = Double.parseDouble(line[2].trim());

                // Create a TwoStringKeys object for the ID combination
                TwoStringKeys key = new TwoStringKeys(id1, id2);

                // Add the ID combination and value to the HashMap
                distances.put(key, distance);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
