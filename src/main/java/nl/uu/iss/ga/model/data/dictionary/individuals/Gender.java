package main.java.nl.uu.iss.ga.model.data.dictionary.individuals;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum Gender implements StringCodeTypeInterface {
    MALE("male"),
    FEMALE("female");

    private final int code;
    private final String stringCode;

    Gender(String code) {
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
