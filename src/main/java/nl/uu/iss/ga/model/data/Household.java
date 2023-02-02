package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.households.HouseholdType;
import main.java.nl.uu.iss.ga.model.data.dictionary.households.StandardizedIncomeGroup;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

import java.util.Map;

public class Household {
    private final Long hid;
    private final int hhSize;
    private final String pc6;
    private final String pc4;
    private final String neighbourhoodCode;
    private final HouseholdType hhType;
    private final StandardizedIncomeGroup standardizedIncomeGroup;
    private final boolean carOwnership;

    public Household(
            Long hid,
            int hhSize,
            String pc4,
            String pc6,
            String neighbourhoodCode,
            HouseholdType hhType,
            StandardizedIncomeGroup standardizedIncomeGroup,
            boolean carOwnership
    ) {
        this.hid = hid;
        this.hhSize = hhSize;
        this.pc4 = pc4;
        this.pc6 = pc6;
        this.neighbourhoodCode = neighbourhoodCode;
        this.hhType = hhType;
        this.standardizedIncomeGroup = standardizedIncomeGroup;
        this.carOwnership = carOwnership;
    }

    public static Household fromCSVLine(Map<String, String> keyValue) {
        return new Household(
                ParserUtil.parseAsLong(keyValue.get("hh_ID")),
                ParserUtil.parseAsInt(keyValue.get("hh_size")),
                keyValue.get("PC4"),
                keyValue.get("PC6"),
                keyValue.get("neighb_code"),
                StringCodeTypeInterface.parseAsEnum(HouseholdType.class, keyValue.get("hh_type")),
                StringCodeTypeInterface.parseAsEnum(StandardizedIncomeGroup.class, keyValue.get("income_group")),
                ParserUtil.parseIntAsBoolean(keyValue.get("car_ownership"))
        );
    }
    public Long getHid() {
        return this.hid;
    }

    public int getHhSize() {
        return this.hhSize;
    }
    public String getPc6() {
        return this.pc6;
    }

    public String getPc4() {
        return this.pc4;
    }

    public HouseholdType getHhType() {
        return this.hhType;
    }

    public StandardizedIncomeGroup getStandardizedIncomeGroup() {
        return this.standardizedIncomeGroup;
    }

    public String getNeighbourhoodCode() {
        return this.neighbourhoodCode;
    }

    public boolean hasCarOwnership() {
        return carOwnership;
    }
}
