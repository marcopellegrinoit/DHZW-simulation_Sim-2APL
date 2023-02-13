package main.java.nl.uu.iss.ga.model.data.dictionary;

import main.java.nl.uu.iss.ga.model.data.ActivityTime;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.CodeTypeInterface;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

import java.util.Map;
import java.util.Objects;

public class LocationEntry {

    // Required for matching
    private final Long pid;
    private final int activityNumber;

    // Redundant. May serve as check
    private final Long hid;
    private final ActivityType activityType;
    private final ActivityTime startTime;

    // Actual location data
    private String lid;
    private double longitude;
    private double latitude;
    private String pc4;
    private String pc6;


    public LocationEntry(Long hid, Long pid, int activityNumber, ActivityType activityType, ActivityTime startTime, String lid, double longitude, double latitude, String pc4, String pc6) {
        this.pid = pid;
        this.activityNumber = activityNumber;
        this.hid = hid;
        this.activityType = activityType;
        this.startTime = startTime;
        this.lid = lid;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pc4 = pc4;
        this.pc6 = pc6;
    }

    public Long getPid() {
        return this.pid;
    }

    public int getActivityNumber() {
        return this.activityNumber;
    }

    public Long getHid() {
        return this.hid;
    }

    public ActivityType getActivityType() {
        return this.activityType;
    }

    public ActivityTime getStartTime() {
        return this.startTime;
    }

    public String getLocationID() {
        return this.lid;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Some norms, e.g. business closure, only apply to non-essential businesses or the like. From these norms,
     * both locations that are essential, and those that are residences can be excluded.
     *
     * @return True if this location can be excluded from a norm only applying to N.E.B. type businesses
     */

    public static LocationEntry fromLine(Map<String, String> keyValue) {
        return new LocationEntry(
                ParserUtil.parseAsLong(keyValue.get("hh_ID")),
                ParserUtil.parseAsLong(keyValue.get("pid")),
                ParserUtil.parseAsInt(keyValue.get("activity_number")),
                StringCodeTypeInterface.parseAsEnum(ActivityType.class, keyValue.get("activity_type")),
                new ActivityTime(ParserUtil.parseAsInt(keyValue.get("start_time"))),
                keyValue.get("lid"),
                ParserUtil.parseAsDouble(keyValue.get("longitude")),
                ParserUtil.parseAsDouble(keyValue.get("latitude")),
                keyValue.get("pc4"),
                keyValue.get("pc6")
        );
    }

    public String getPc4() {
        return pc4;
    }

    public String getPc6() {
        return pc6;
    }

    public boolean isInsideDHZW() {
        return !this.lid.equals("outside_DHZW");
    }

    public void setToTrainStation(){
        this.lid = "station_Moerwijk";
        this.longitude = 52.01678596;
        this.latitude = 4.30776639;
        this.pc4 = "2532";
        this.pc6 = "2532CP";
    }
    public boolean isTrainStation(){
        return this.lid.equals("station_Moerwijk");
    }

}