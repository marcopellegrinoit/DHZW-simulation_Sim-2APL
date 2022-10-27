package main.java.nl.uu.iss.ga.model.data.dictionary;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;

import java.util.Map;

public class GeoLocation {

    private final String pc6;
    private final String pc4;
    private final String neighbourhoodCode;

    private final double longitude;
    private final double latitude;

    public GeoLocation(String pc6, String pc4, String neighbourhoodCode, double longitude, double latitude) {
        this.pc6 = pc6;
        this.pc4 = pc4;
        this.neighbourhoodCode = neighbourhoodCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static GeoLocation fromLine(Map<String, String> keyValue) {
        return new GeoLocation(
                keyValue.get("PC6"),
                keyValue.get("PC4"),
                keyValue.get("neighb_code"),
                ParserUtil.parseAsDouble(keyValue.get("longitude")),
                ParserUtil.parseAsDouble(keyValue.get("latitude"))
        );
    }

    public String getPc6() {
        return this.pc6;
    }

    public String getPc4() {
        return this.pc4;
    }

    public String getNeighbourhoodCode() {
        return this.neighbourhoodCode;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }
}
