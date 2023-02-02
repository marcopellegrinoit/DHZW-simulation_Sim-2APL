package main.java.nl.uu.iss.ga.model.data.dictionary;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum ActivityType implements StringCodeTypeInterface {

    HOME("home"),
    WORK("work"),
    SHOPPING("shopping"),
    SCHOOL("school"),
    SPORT("sport");
    private final int code;
    private final String stringCode;

    ActivityType(String code) {
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
