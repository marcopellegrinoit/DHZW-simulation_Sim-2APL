package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.StringCodeTypeInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.util.Map;

public class Person implements Context {
    private static final String VA_PERSON_HEADERS =
            "hid,pid,serialno,person_number,record_type,age,relationship,sex,school_enrollment,grade_level_attending,employment_status,employment_socp,cell_id,designation";
    private static final String[] VA_PERSON_HEADER_INDICES = VA_PERSON_HEADERS.split(ParserUtil.SPLIT_CHAR);

    private final Household household;
    private final Long pid;
    private final int age;
    private final Gender gender;
    private final MigrationBackground migrationBackground;
    private final Boolean isChild;
    private final EducationCurrent currentEducation;
    private final EducationAttainment educationAttainment;


    // Original constructor
    public Person(
            Household household,
            Long pid,
            int age,
            Gender gender,
            MigrationBackground migrationBackground,
            Boolean isChild,
            EducationCurrent currentEducation,
            EducationAttainment educationAttainment) {
        this.household = household;
        this.pid = pid;
        this.age = age;
        this.gender = gender;
        this.migrationBackground = migrationBackground;
        this.isChild = isChild;
        this.currentEducation = currentEducation;
        this.educationAttainment = educationAttainment;
    }

    public static Person fromLine(Map<Long, Household> households, Map<String, String> keyValue) {
        return new Person(
                households.get(ParserUtil.parseAsLong(keyValue.get("hid"))),
                ParserUtil.parseAsLong(keyValue.get("pid")),
                ParserUtil.parseAsInt(keyValue.get("age")),
                StringCodeTypeInterface.parseAsEnum(Gender.class, keyValue.get("gender")),
                StringCodeTypeInterface.parseAsEnum(MigrationBackground.class, keyValue.get("migration_background")),
                ParserUtil.parseIntAsBoolean(keyValue.get("is_child")),
                StringCodeTypeInterface.parseAsEnum(EducationCurrent.class, keyValue.get("current_education")),
                StringCodeTypeInterface.parseAsEnum(EducationAttainment.class, keyValue.get("edu_attainment")));
    }

    public Household getHousehold() {
        return this.household;
    }

    public Long getPid() {
        return this.pid;
    }

    public int getAge() {
        return this.age;
    }

    public Gender getGender() {
        return this.gender;
    }

    public MigrationBackground getMigrationBackground() {
        return this.migrationBackground;
    }

    public Boolean getIsChild() {
        return this.isChild;
    }

    public EducationCurrent getCurrentEducation() {
        return this.currentEducation;
    }


    public EducationAttainment getEducationAttainment() {
        return this.educationAttainment;
    }

}
