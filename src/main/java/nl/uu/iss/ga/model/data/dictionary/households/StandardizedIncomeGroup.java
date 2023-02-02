package main.java.nl.uu.iss.ga.model.data.dictionary.households;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

// Standardized income: disposable income corrected for differences in household size and composition

public enum StandardizedIncomeGroup implements StringCodeTypeInterface {
    INCOME_GROUP_1_10("income_1_10"),
    INCOME_GROUP_2_10("income_2_10"),
    INCOME_GROUP_3_10("income_3_10"),
    INCOME_GROUP_4_10("income_4_10"),
    INCOME_GROUP_5_10("income_5_10"),
    INCOME_GROUP_6_10("income_6_10"),
    INCOME_GROUP_7_10("income_7_10"),
    INCOME_GROUP_8_10("income_8_10"),
    INCOME_GROUP_9_10("income_9_10"),
    INCOME_GROUP_10_10("income_10_10");

    private final int code;
    private final String stringCode;

    StandardizedIncomeGroup(String code) {
        this.stringCode = code;
        this.code = StringCodeTypeInterface.parseStringcode(code);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getStringCode() {
        return stringCode;
    }
}
