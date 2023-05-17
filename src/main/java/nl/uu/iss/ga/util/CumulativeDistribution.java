package main.java.nl.uu.iss.ga.util;

import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.HashMap;
import java.util.Random;

public class CumulativeDistribution {

    public static TransportMode sampleWithCumulativeDistribution(HashMap<TransportMode, Double> choiceProbabilities){
        // calculate the cumulative proportions
        double[] cumulativeProportions = new double[choiceProbabilities.size()];
        double cumulativeSum = 0.0;
        int index = 0;
        for (double proportion : choiceProbabilities.values()) {
            cumulativeSum += proportion;
            cumulativeProportions[index++] = cumulativeSum;
        }

        // sample a random number
        Random random = new Random(System.currentTimeMillis());

        // Generate a random number between 0 and 1
        double randomValue = random.nextDouble();

        // Find the index corresponding to the sampled key
        int sampledIndex = 0;
        while (sampledIndex < cumulativeProportions.length &&
                randomValue > cumulativeProportions[sampledIndex]) {
            sampledIndex++;
        }

        // Get the corresponding key
        return (TransportMode) choiceProbabilities.keySet().toArray()[sampledIndex];
    }
}
