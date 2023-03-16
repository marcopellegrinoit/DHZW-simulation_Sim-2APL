package main.java.nl.uu.iss.ga.model.data.dictionary;

import java.util.Objects;

public class TwoStringKeys {
    private static final String DELIMITER = "|";
    private String key1;
    private String key2;
    private String concatenatedKeys;

    public TwoStringKeys(String key1, String key2) {
        // Sort the keys in alphabetical order
        if (key1.compareTo(key2) < 0) {
            this.key1 = key1;
            this.key2 = key2;
        } else {
            this.key1 = key2;
            this.key2 = key1;
        }
        // Concatenate the keys with a delimiter
        this.concatenatedKeys = this.key1 + DELIMITER + this.key2;
    }

    public String getKey1(){
        return this.key1;
    }

    public String getKey2(){
        return this.key2;
    }

    // Override equals and hashCode methods for TwoStringKeys class
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TwoStringKeys other = (TwoStringKeys) obj;
        if (!Objects.equals(this.concatenatedKeys, other.concatenatedKeys)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.concatenatedKeys);
        return hash;
    }
}