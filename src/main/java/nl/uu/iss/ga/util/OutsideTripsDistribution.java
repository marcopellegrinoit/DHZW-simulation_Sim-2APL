package main.java.nl.uu.iss.ga.util;


import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;

import java.util.HashMap;

public class OutsideTripsDistribution {
    private static final HashMap<TransportMode, Double> schoolDistribution = new HashMap<TransportMode, Double>();
    private static final HashMap<TransportMode, Double> shoppingDistribution = new HashMap<TransportMode, Double>();
    private static final HashMap<TransportMode, Double> sportDistribution = new HashMap<TransportMode, Double>();
    private static final HashMap<TransportMode, Double> workDistribution = new HashMap<TransportMode, Double>();

    static {
        schoolDistribution.put(TransportMode.BIKE, 0.35849057);
        schoolDistribution.put(TransportMode.BUS_TRAM, 0.33018868);
        schoolDistribution.put(TransportMode.CAR_DRIVER, 0.23584906/2);
        schoolDistribution.put(TransportMode.CAR_PASSENGER, 0.23584906/2);
        schoolDistribution.put(TransportMode.TRAIN, 0.04716981);
        schoolDistribution.put(TransportMode.WALK, 0.04716981);

        shoppingDistribution.put(TransportMode.BIKE, 0.14393939);
        shoppingDistribution.put(TransportMode.BUS_TRAM, 0.18181818);
        shoppingDistribution.put(TransportMode.CAR_DRIVER, 0.48484848/2);
        shoppingDistribution.put(TransportMode.CAR_PASSENGER, 0.48484848/2);
        shoppingDistribution.put(TransportMode.TRAIN, 0.01515152);
        shoppingDistribution.put(TransportMode.WALK, 0.17424242);

        sportDistribution.put(TransportMode.BIKE, 0.17142857);
        sportDistribution.put(TransportMode.BUS_TRAM, 0.02857143);
        sportDistribution.put(TransportMode.CAR_DRIVER, 0.74285714/2);
        sportDistribution.put(TransportMode.CAR_PASSENGER, 0.74285714/2);
        sportDistribution.put(TransportMode.TRAIN, 0.0);
        sportDistribution.put(TransportMode.WALK, 0.05714286);

        workDistribution.put(TransportMode.BIKE, 0.18636364);
        workDistribution.put(TransportMode.BUS_TRAM, 0.20909091);
        workDistribution.put(TransportMode.CAR_DRIVER, 0.50909091);
        workDistribution.put(TransportMode.CAR_PASSENGER, 0.50909091/2);
        workDistribution.put(TransportMode.TRAIN, 0.07272727);
        workDistribution.put(TransportMode.WALK, 0.02272727);
    }

    public static HashMap<TransportMode, Double> getOutsideTripsDistribution(ActivityType activityType){
        switch (activityType){
            case SCHOOL:
                return schoolDistribution;
            case SHOPPING:
                return shoppingDistribution;
            case SPORT:
                return sportDistribution;
            case WORK:
                return workDistribution;
            default:
                return null;
        }
    }

}
