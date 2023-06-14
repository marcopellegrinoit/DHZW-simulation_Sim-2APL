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

public class RoutingTrainReader {

    private static final Logger LOGGER = Logger.getLogger(RoutingTrainReader.class.getName());
    private final HashMap<String, HashMap<String, Double>> trainTimes;
    private final HashMap<String, HashMap<String, Double>> trainDistances;
    private final HashMap<String, HashMap<String, Double>> walkTimes;
    private final HashMap<String, HashMap<String, Double>> busTimes;
    private final HashMap<String, HashMap<String, Double>> busDistances;
    private final HashMap<String, HashMap<String, Integer>> nChanges;
    private final HashMap<String, HashMap<String, String>> postcodeStops;
    private final HashMap<String, HashMap<String, Integer>> feasibleFlags;
    private HashMap<String, HashMap<String, Double>> totalDistances;


    public RoutingTrainReader(List<File> routingFile) {
        this.trainTimes = new HashMap<>();
        this.trainDistances = new HashMap<>();
        this.walkTimes = new HashMap<>();
        this.busTimes = new HashMap<>();
        this.busDistances = new HashMap<>();
        this.nChanges = new HashMap<>();
        this.postcodeStops = new HashMap<>();
        this.feasibleFlags = new HashMap<>();
        this.totalDistances = new HashMap<>();

        for(File f : routingFile) {
            readTravelTimes(f);
        }
    }


    public Double getTrainTime(String location1, String location2) {
        return this.trainTimes.get(location1).get(location2);
    }
    public Double getTrainDistance(String location1, String location2) {
        return this.trainDistances.get(location1).get(location2);
    }
    public Double getWalkTime(String location1, String location2) {
        return this.walkTimes.get(location1).get(location2);
    }
    public Double getBusTime(String location1, String location2) {
        return this.busTimes.get(location1).get(location2);
    }
    public Double getBusDistance(String location1, String location2) {
        return this.busDistances.get(location1).get(location2);
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
        LOGGER.log(Level.INFO, "Reading routing train file " + routingFile.toString());

        try {
            CSVReader reader = new CSVReader(new FileReader(routingFile));
            String[] line;
            // Skip the first line of the CSV file (the header)
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // Parse the ID combination and value from the CSV line
                String departure = line[0].trim();
                String arrival = line[1].trim();

                double walkTime = Double.parseDouble(line[9].trim());
                double trainTime = Double.parseDouble(line[11].trim());
                double trainDistance = Double.parseDouble(line[17].trim());
                double busTime = Double.parseDouble(line[10].trim());
                double busDistance = Double.parseDouble(line[16].trim());
                int nChange = Integer.parseInt(line[13].trim());
                double distanceTotal = Double.parseDouble(line[14].trim());
                String postcodeStop = line[19].trim();
                int feasibleFlag = Integer.parseInt(line[20].trim());

                // Add to the HashMaps
                if(this.trainTimes.containsKey(departure)) {
                    // if there is already info for the departure
                    this.trainTimes.get(departure).put(arrival, trainTime);
                    this.trainDistances.get(departure).put(arrival, trainDistance);
                    this.walkTimes.get(departure).put(arrival, walkTime);
                    this.busTimes.get(departure).put(arrival, busTime);
                    this.busDistances.get(departure).put(arrival, busDistance);
                    this.nChanges.get(departure).put(arrival, nChange);
                    this.postcodeStops.get(departure).put(arrival, postcodeStop);
                    this.feasibleFlags.get(departure).put(arrival, feasibleFlag);
                    this.totalDistances.get(departure).put(arrival, distanceTotal);
                } else {
                    // create the whole structure for such departure node
                    HashMap <String, Double> tmp = new HashMap<String, Double>();
                    tmp.put(arrival, trainTime);
                    this.trainTimes.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, trainDistance);
                    this.trainDistances.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, walkTime);
                    this.walkTimes.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, busTime);
                    this.busTimes.put(departure, tmp);

                    tmp.clear();
                    tmp.put(arrival, busDistance);
                    this.busDistances.put(departure, tmp);

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
                    tmp.put(arrival, distanceTotal);
                    this.totalDistances.put(departure, tmp);
                }

            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
