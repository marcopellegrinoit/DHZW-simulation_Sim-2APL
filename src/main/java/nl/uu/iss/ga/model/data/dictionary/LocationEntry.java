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
    private String postcode;
    private boolean isInDHZW;


    public LocationEntry(Long hid, Long pid, int activityNumber, ActivityType activityType, ActivityTime startTime, String postcode, boolean isInDHZW) {
        this.pid = pid;
        this.activityNumber = activityNumber;
        this.hid = hid;
        this.activityType = activityType;
        this.startTime = startTime;
        this.postcode = postcode;
        this.isInDHZW = isInDHZW;
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

    public String getPostcode() {
        return this.postcode;
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
                new ActivityTime(ParserUtil.parseAsInt(keyValue.get("start_time_seconds"))),
                keyValue.get("postcode"),
                ParserUtil.parseIntAsBoolean(keyValue.get("in_DHZW"))
        );
    }

    public boolean isInDHZW() {
        return this.isInDHZW;
    }

}