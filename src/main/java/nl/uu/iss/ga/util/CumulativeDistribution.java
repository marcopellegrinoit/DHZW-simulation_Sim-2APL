package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.HashMap;

public class CumulativeDistribution {

    public static TransportMode sampleWithCumulativeDistribution(HashMap<TransportMode, Double> choiceProbabilities){
        double r = Math.random();
        double cumulativeSum = 0.0;
        for (TransportMode transportMode: choiceProbabilities.keySet()){
            cumulativeSum += choiceProbabilities.get(transportMode);
            if(r < cumulativeSum) {
                return transportMode;
            }
        }
        return null;
    }
}
