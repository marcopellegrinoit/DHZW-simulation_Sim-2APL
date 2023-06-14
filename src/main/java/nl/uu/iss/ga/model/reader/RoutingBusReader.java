package main.java.nl.uu.iss.ga.model.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoutingBusReader {

    private static final Logger LOGGER = Logger.getLogger(RoutingBusReader.class.getName());
    private final HashMap<String, HashMap<String, Double>> busTimes;
    private final HashMap<String, HashMap<String, Double>> busDistances;
    private final HashMap<String, HashMap<String, Double>> walkTimes;
    private final HashMap<String, HashMap<String, Integer>> nChanges;
    private final HashMap<String, HashMap<String, String>> postcodeStops;
    private final HashMap<String, HashMap<String, Integer>> feasibleFlags;
    private HashMap<String, HashMap<String, Double>> totalDistances;


    public RoutingBusReader(List<File> routingFile) {
        this.busTimes = new HashMap<>();
        this.walkTimes = new HashMap<>();
        this.busDistances = new HashMap<>();
        this.nChanges = new HashMap<>();
        this.postcodeStops = new HashMap<>();
        this.feasibleFlags = new HashMap<>();
        this.totalDistances = new HashMap<>();

        for(File f : routingFile) {
            readTravelTimes(f);
        }
    }


    public Double getBusTime(String location1, String location2) {
        return this.busTimes.get(location1).get(location2);
    }
    public Double getBusDistance(String location1, String location2) {
        return this.busDistances.get(location1).get(location2);
    }
    public Double getWalkTime(String location1, String location2) {
        return this.walkTimes.get(location1).get(location2);
    }
    public int getChange(String location1, String location2) {
        return this.nChanges.get(location1).get(location2);
    }
    public String getPostcodeStop(String location1, String location2) {
        return this.postcodeStops.get(location1).get(location2);
    }
    public int getFeasibleFlag(String location1, String location2) {
        return this.feasibleFlags.get(location1).get(location2);
    }
    public Double getTotalDistance(String location1, String location2) {
        return this.totalDistances.get(location1).get(location2);
    }

    private void readTravelTimes(File routingFile) {
        LOGGER.log(Level.INFO, "Reading routing bus file " + routingFile.toString());

        try {
            CSVReader reader = new CSVReader(new FileReader(routingFile));
            String[] line;
            // Skip the first line of the CSV file (the header)
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // Parse the ID combination and value from the CSV line
                String departure = line[0].trim();
                String arrival = line[1].trim();

                double busTime = Double.parseDouble(line[10].trim());
                double busDistance = Double.parseDouble(line[15].trim());
                double walkTime = Double.parseDouble(line[9].trim());
                int nChange = Integer.parseInt(line[12].trim());
                double totalDistance = Double.parseDouble(line[13].trim());
                String postcodeStop = line[17].trim();
                int feasibleFlag = Integer.parseInt(line[18].trim());


                // Add to the HashMaps
                if(this.busTimes.containsKey(departure)) {
                    // if there is already info for the departure
                    this.busTimes.get(departure).put(arrival, busTime);
                    this.busDistances.get(departure).put(arrival, busDistance);
                    this.walkTimes.get(departure).put(arrival, walkTime);
                    this.nChanges.get(departure).put(arrival, nChange);
                    this.postcodeStops.get(departure).put(arrival, postcodeStop);
                    this.feasibleFlags.get(departure).put(arrival, feasibleFlag);
                    this.totalDistances.get(departure).put(arrival, totalDistance);
                } else {
                    // create the whole structure for such departure node
                    HashMap <String, Double> tmp = new HashMap<String, Double>();
                    tmp.put(arrival, busTime);
                    this.busTimes.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, busDistance);
                    this.busDistances.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, walkTime);
                    this.walkTimes.put(departure, tmp);

                    HashMap <String, Integer> tmpInt = new HashMap<String, Integer>();
                    tmpInt.put(arrival, nChange);
                    this.nChanges.put(departure, tmpInt);

                    HashMap <String, String> tmpString = new HashMap<String, String>();
                    tmpString.put(arrival, postcodeStop);
                    this.postcodeStops.put(departure, tmpString);

                    tmpInt.clear();
                    tmpInt.put(arrival, feasibleFlag);
                    this.feasibleFlags.put(departure, tmpInt);

                    tmp.clear();
                    tmp.put(arrival, totalDistance);
                    this.totalDistances.put(departure, tmp);
                }

            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
