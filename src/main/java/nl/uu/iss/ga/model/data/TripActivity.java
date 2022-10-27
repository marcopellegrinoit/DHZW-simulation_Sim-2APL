package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.ActivityType;
import main.java.nl.uu.iss.ga.model.data.dictionary.TransportMode;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.CodeTypeInterface;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;

import java.util.Map;

public class TripActivity extends Activity {
    private final TransportMode transportMode;
    private final boolean personWasDriver;
    private final boolean personWasPassenger;
    private final String pc4Start;
    private final String pc4End;

    public TripActivity(
            long pid,
            long hid,
            int activityNumber,
            ActivityType activityType,
            ActivityTime startTime,
            int duration,
            String pc4,
            TransportMode transportMode,
            boolean personWasDriver,
            boolean personWasPassenger,
            String pc4Start,
            String pc4End
    ) {
        super(pid, hid, activityNumber, activityType, startTime, duration);
        this.transportMode = transportMode;
        this.personWasDriver = personWasDriver;
        this.personWasPassenger = personWasPassenger;
        this.pc4Start = pc4Start;
        this.pc4End = pc4End;
    }

    public TripActivity(Activity baseActivity, TransportMode transportMode, boolean personWasDriver, boolean personWasPassenger, String pc4Start, String pc4End) {
        super(
                baseActivity.getPid(),
                baseActivity.getHid(),
                baseActivity.getActivityNumber(),
                baseActivity.getActivityType(),
                baseActivity.getStartTime(),
                baseActivity.getDuration()
        );
        this.transportMode = transportMode;
        this.personWasDriver = personWasDriver;
        this.personWasPassenger = personWasPassenger;
        this.pc4Start = pc4Start;
        this.pc4End = pc4End;
    }

    public TransportMode getMode() {
        return this.transportMode;
    }

    public boolean isPersonWasDriver() {
        return this.personWasDriver;
    }

    public boolean isPersonWasPassenger() {
        return this.personWasPassenger;
    }

    public static TripActivity fromLine(Activity baseActivity, Map<String, String> keyValue) {
        return new TripActivity(
                baseActivity,
                CodeTypeInterface.parseAsEnum(TransportMode.class, keyValue.get("modal_choice")),
                ParserUtil.parseIntAsBoolean(keyValue.get("driver_flag")),
                ParserUtil.parseIntAsBoolean(keyValue.get("passenger_flag")),
                keyValue.get("PC4_start"),
                keyValue.get("PC4_end")
        );
    }

    @Override
    public TripActivity clone() {
        return new TripActivity(
                super.clone(),
                this.transportMode,
                this.personWasDriver,
                this.personWasPassenger,
                this.pc4Start,
                this.pc4End
        );
    }

    @Override
    public String toString() {
        return "TripActivity{" +
                "transportMode=" + transportMode +
                ", personWasDriver=" + personWasDriver +
                ", personWasPassenger=" + personWasPassenger +
                '}';
    }
}
