package main.java.nl.uu.iss.ga.model.data.dictionary.individuals;

import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;

public enum EducationAttainment implements StringCodeTypeInterface {
    NOTHING("nothing"),
    LOW("low"),
    MIDDLE("middle"),
    HIGH("high");

    private final int code;
    private final String stringCode;

    EducationAttainment(String code) {
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
