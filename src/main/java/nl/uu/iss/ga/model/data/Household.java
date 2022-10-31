package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;

import java.util.Map;

public class Household {
    private final Long hid;

    private final int hhSize;
    private final GeoLocation location;
    private final long locationID;

    public Household(
            Long hid,
            int hhSize,
            GeoLocation location,
            long locationID
    ) {
        this.hid = hid;
        this.hhSize = hhSize;
        this.location = location;
        this.locationID = locationID;
    }

    public Long getHid() {
        return this.hid;
    }

    public int getHhSize() {
        return this.hhSize;
    }

    public GeoLocation getLocation() {
        return this.location;
    }

    public static Household fromCSVLine(Map<String, String> keyValue) {
        return new Household(
                ParserUtil.parseAsLong(keyValue.get("hid")),
                ParserUtil.parseAsInt(keyValue.get("hh_size")),
                new GeoLocation(keyValue.get("PC6"),
                        keyValue.get("PC4"),
                        keyValue.get("neighb_code"),
                        ParserUtil.parseAsDouble(keyValue.get("longitude")),
                        ParserUtil.parseAsDouble(keyValue.get("latitude"))),
                ParserUtil.parseAsLong(keyValue.get("hid"))
        );
    }

    public long getLocationID() {
        return locationID;
    }

}
