package main.java.nl.uu.iss.ga.model.data.dictionary.households;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum HouseholdType implements StringCodeTypeInterface {
    SINGLE("single"),
    SINGLE_PARENT("single-parent"),
    COUPLE_WITH_CHILDREN("couple_with_children"),
    COUPLE_WITHOUT_CHILDREN("couple_without_children");

    private final int code;
    private final String stringCode;

    HouseholdType(String code) {
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
