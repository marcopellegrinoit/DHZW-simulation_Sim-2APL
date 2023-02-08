package main.java.nl.uu.iss.ga.model.data.dictionary;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum TransportMode implements StringCodeTypeInterface {
    CAR_DRIVER("car_driver"),
    CAR_PASSENGER("car_passenger"),
    BIKE("bike"),
    BUS_TRAM("bus_tram"),
    TRAIN("train"),
    WALK("walk");

    private final int code;
    private final String stringCode;

    TransportMode(String code) {
        this.stringCode = code;

        // Transform String into integer code for comparable
        this.code = StringCodeTypeInterface.parseStringcode(code);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getStringCode() {
        return this.stringCode;
    }
}
