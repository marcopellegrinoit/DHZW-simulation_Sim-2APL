package main.java.nl.uu.iss.ga.model.data.dictionary;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum MigrationBackground implements StringCodeTypeInterface {
    DUTCH("Dutch"),  // (GQ/not a one-family house or mobile home)
    WESTERN("Western"),
    NON_WESTERN("Non_Western");

    private final int code;
    private final String stringCode;

    MigrationBackground(String code) {
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
