package main.java.nl.uu.iss.ga.model.data;

import main.java.nl.uu.iss.ga.model.data.dictionary.*;
import main.java.nl.uu.iss.ga.model.data.dictionary.util.ParserUtil;

import java.util.Map;

public class Household {
    private final Long hid;

    private final int hhSize;
    private final String pc6;
    private final String pc4;
    private final String neighb_code;

    public Household(
            Long hid,
            int hhSize,
            String pc4,
            String pc6,
            String neighb_code
    ) {
        this.hid = hid;
        this.hhSize = hhSize;
        this.pc4 = pc4;
        this.pc6 = pc6;
        this.neighb_code = neighb_code;
    }

    public Long getHid() {
        return this.hid;
    }

    public int getHhSize() {
        return this.hhSize;
    }

    public static Household fromCSVLine(Map<String, String> keyValue) {
        return new Household(
                ParserUtil.parseAsLong(keyValue.get("hh_ID")),
                ParserUtil.parseAsInt(keyValue.get("hh_size")),
                keyValue.get("PC4"),
                keyValue.get("PC6"),
                keyValue.get("neighb_code")
        );
    }
}
