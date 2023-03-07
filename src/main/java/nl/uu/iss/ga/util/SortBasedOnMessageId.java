package main.java.nl.uu.iss.ga.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class SortBasedOnMessageId implements Comparator<JSONObject> {
    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * lhs- 1st message in the form of json object. rhs- 2nd message in the form
     * of json object.
     */
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        try {
            return Integer.compare(lhs
                    .getInt("duration"), rhs.getInt("duration"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;

    }
}