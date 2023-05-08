package main.java.nl.uu.iss.ga.util.tracking;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.DayOfWeek;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ModeOfTransportTracker {

    private Map<TransportMode, AtomicInteger> totalModeMap;
    private AtomicInteger[][] modeDayMap = new AtomicInteger[DayOfWeek.values().length][TransportMode.values().length];
    private AtomicInteger[][] modeActivityMap = new AtomicInteger[ActivityType.values().length][TransportMode.values().length];
    private AtomicInteger[][] modeCarLicenseMap = new AtomicInteger[2][TransportMode.values().length];
    private AtomicInteger[][] modeCarOwnershipMap = new AtomicInteger[2][TransportMode.values().length];

    public void reset() {
        // Initialise map for overall mode frequencies
        totalModeMap = new ConcurrentHashMap<>();
        for(TransportMode mode : TransportMode.values()) {
            totalModeMap.put(mode, new AtomicInteger(0));
        }

        // Initialise map for mode x day frequencies
        for (int i = 0; i < this.modeDayMap.length; i++) {
            for (int j = 0; j < this.modeDayMap[i].length; j++) {
                this.modeDayMap[i][j] = new AtomicInteger(0);
            }
        }

        // Initialise map for mode x activity frequencies
        for (int i = 0; i < this.modeActivityMap.length; i++) {
            for (int j = 0; j < this.modeActivityMap[i].length; j++) {
                this.modeActivityMap[i][j] = new AtomicInteger(0);
            }
        }

        for (int i = 0; i < this.modeCarLicenseMap.length; i++) {
            for (int j = 0; j < this.modeCarLicenseMap[i].length; j++) {
                this.modeCarLicenseMap[i][j] = new AtomicInteger(0);
            }
        }

        for (int i = 0; i < this.modeCarOwnershipMap.length; i++) {
            for (int j = 0; j < this.modeCarOwnershipMap[i].length; j++) {
                this.modeCarOwnershipMap[i][j] = new AtomicInteger(0);
            }
        }
    }

    public void notifyTransportModeUsed(TransportMode mode, DayOfWeek day, ActivityType activityType, boolean hasCarLicense, boolean hasCar) {
        this.totalModeMap.get(mode).getAndIncrement();
        this.modeDayMap[day.ordinal()][mode.ordinal()].getAndIncrement();
        this.modeActivityMap[activityType.ordinal()][mode.ordinal()].getAndIncrement();
        this.modeActivityMap[activityType.ordinal()][mode.ordinal()].getAndIncrement();
        this.modeCarLicenseMap[hasCarLicense ? 1 : 0][mode.ordinal()].getAndIncrement();
        this.modeCarOwnershipMap[hasCar ? 1 : 0][mode.ordinal()].getAndIncrement();
    }

    public Map<TransportMode, AtomicInteger> getTotalModeMap() {
        return totalModeMap;
    }
    public AtomicInteger[][] getModeDayMap() {
        return this.modeDayMap;
    }
    public AtomicInteger[][] getModeActivityMap() {
        return this.modeActivityMap;
    }
    public AtomicInteger[][] getModeCarLicenseMap() {
        return this.modeCarLicenseMap;
    }
    public AtomicInteger[][] getModeCarOwnershipMap() {
        return this.modeCarOwnershipMap;
    }

    public void saveTotalModeToCsv(File outputDir) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File(outputDir, "mode_total.csv")));

        String[] row = new String[2];
        row[0] = "mode_choice";
        row[1] = "frequency";

        writer.writeNext(row);
        for (TransportMode mode : TransportMode.values()) {
            row = new String[2];
            row[0] = String.valueOf(mode);
            row[1] = String.valueOf(this.totalModeMap.get(mode));
            writer.writeNext(row);
        }
        writer.close();
    }
    public void saveModeDayToCsv(File outputDir) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File(outputDir, "mode-day.csv")));

        String[] row = new String[3];
        row[0] = "day";
        row[1] = "mode_choice";
        row[2] = "frequency";

        writer.writeNext(row);
        for (DayOfWeek day : DayOfWeek.values()) {
            row = new String[3];
            row[0] = String.valueOf(day);
            for (TransportMode mode : TransportMode.values()) {
                row[1] = String.valueOf(mode);
                AtomicInteger value = this.modeDayMap[day.ordinal()][mode.ordinal()];
                row[2] = String.valueOf(value.get());
                writer.writeNext(row);
            }
        }
        writer.close();
    }
    public void saveModeActivityToCsv(File outputDir) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File( outputDir,"mode-activity.csv")));

        String[] row = new String[3];
        row[0] = "activity";
        row[1] = "mode_choice";
        row[2] = "frequency";

        writer.writeNext(row);
        for (ActivityType activityType : ActivityType.values()) {
            row = new String[3];
            row[0] = String.valueOf(activityType);
            for (TransportMode mode : TransportMode.values()) {
                row[1] = String.valueOf(mode);
                AtomicInteger value = this.modeActivityMap[activityType.ordinal()][mode.ordinal()];
                row[2] = String.valueOf(value.get());
                writer.writeNext(row);
            }
        }
        writer.close();
    }
    public void saveModeCarLicenseToCsv(File outputDir) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File( outputDir,"mode-car_license.csv")));

        String[] row = new String[3];
        row[0] = "car_license";
        row[1] = "mode_choice";
        row[2] = "frequency";

        writer.writeNext(row);
        for (boolean b : new boolean[] { false, true }) {
            row = new String[3];
            row[0] = String.valueOf(b);
            for (TransportMode mode : TransportMode.values()) {
                row[1] = String.valueOf(mode);
                AtomicInteger value = this.modeCarLicenseMap[b ? 1 : 0][mode.ordinal()];
                row[2] = String.valueOf(value.get());
                writer.writeNext(row);
            }
        }
        writer.close();
    }
    public void saveModeCarOwnershipToCsv(File outputDir) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File( outputDir,"mode-car_ownership.csv")));

        String[] row = new String[3];
        row[0] = "car_license";
        row[1] = "mode_choice";
        row[2] = "frequency";

        writer.writeNext(row);
        for (boolean b : new boolean[] { false, true }) {
            row = new String[3];
            row[0] = String.valueOf(b);
            for (TransportMode mode : TransportMode.values()) {
                row[1] = String.valueOf(mode);
                AtomicInteger value = this.modeCarOwnershipMap[b ? 1 : 0][mode.ordinal()];
                row[2] = String.valueOf(value.get());
                writer.writeNext(row);
            }
        }
        writer.close();
    }

    public void appendOutput(File outputDir) throws IOException, CsvValidationException {
        // Calculate the sum of all frequencies
        int totalFrequency = 0;
        for (AtomicInteger frequency : this.totalModeMap.values()) {
            totalFrequency += frequency.get();
        }

        // Create a new map to store the proportions
        Map<TransportMode, Double> proportionMap = new HashMap<>();

        // Iterate over the map and calculate the proportions
        for (Map.Entry<TransportMode, AtomicInteger> entry : this.totalModeMap.entrySet()) {
            double proportion = entry.getValue().get() / (double)totalFrequency;
            proportionMap.put(entry.getKey(), proportion);
        }

        // Create a new CSV reader
        CSVReader reader = new CSVReader(new FileReader(new File(outputDir, "output_proportions.csv")));

        // Read the header row of the CSV file
        String[] headerRow = reader.readNext();

        // Create a new CSV writer
        CSVWriter writer = new CSVWriter(new FileWriter(new File(outputDir, "output_proportions.csv"), true));

        // Create an array to hold the row data
        String[] rowData = new String[headerRow.length];

        // Populate the array with the proportions
        for (Map.Entry<TransportMode, Double> entry : proportionMap.entrySet()) {
            String key = entry.getKey().toString();
            Double value = entry.getValue();
            for (int i = 0; i < headerRow.length; i++) {
                if (headerRow[i].equals(key)) {
                    rowData[i] = Double.toString(value);
                }
            }
        }

        // Write the row data to the CSV file
        writer.writeNext(rowData);

        // Close the writer and reader
        writer.close();
        reader.close();
    }

}