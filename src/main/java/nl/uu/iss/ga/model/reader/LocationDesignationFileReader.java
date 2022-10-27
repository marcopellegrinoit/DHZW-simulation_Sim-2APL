package main.java.nl.uu.iss.ga.model.reader;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocationDesignationFileReader {
    private static final Logger LOGGER = Logger.getLogger(LocationDesignationFileReader.class.getName());

    private List<File> locationDesignationFiles;

    public LocationDesignationFileReader(List<File> locationDesignationFiles) {
        this.locationDesignationFiles = locationDesignationFiles;

        for(File f : this.locationDesignationFiles) {
            readLocationDesignations(f);
        }
    }

    private void readLocationDesignations(File locationDesignationFile) {
        LOGGER.log(Level.INFO, "Reading location designation file " + locationDesignationFile.toString());
        try (
            FileInputStream is = new FileInputStream(locationDesignationFile);
            Scanner s = new Scanner(is)
        ) {
            iterateLocationDesignations(s);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read location designation file " + locationDesignationFile.toString(), e);
        }
    }

    private void iterateLocationDesignations(Scanner s) {
        String header = s.nextLine();
        String[] headerIndices = header.split(ParserUtil.SPLIT_CHAR);
        while(s.hasNextLine()) {
            Map<String, String> locationDesignationEntry = ParserUtil.zipLine(headerIndices, s.nextLine());
        }
    }

}
