package tv.superawesome.lib.sanetwork.request;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class SANetworkUtils {

    /**
     * This method checks all possibilities to determine if a passed JSONObject is null or empty.
     *
     * @param dict  a JSONObject that will be checked for emptiness / validity
     * @return      either true or false, if conditions are met
     */
    boolean isJSONEmpty(JSONObject dict) {
        return dict == null || dict.length() == 0 || dict.toString().equals("{}");
    }

    /**
     * This method takes a JSONObject paramter and returns it as a valid GET query string
     * (e.g. a JSON { "name": "John", "age": 23 } would become "name=John&age=23"
     *
     * @param dict  a JSON object to be transformed into a GET query string
     * @return      a valid GET query string
     */
    String formGetQueryFromDict(JSONObject dict) {
        // string to be returned
        String queryString = "";

        // if the JSONObject is null or empty, then just return the empty queryString
        if (isJSONEmpty(dict)) return queryString;

        // if not, proceed to create an Array list of strings in the format
        // "key=value&", taken from the keys and values found in the JSONObject
        ArrayList<String> queryArray = new ArrayList<>();
        Iterator<String> keys = dict.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object valObj = dict.opt(key);
            String val = (valObj != null ? valObj.toString().replace("\"","") : "");

            queryArray.add(key + "=" + val + "&");
        }

        // add the values in the array to the final query string
        for (String queryObj : queryArray) {
            queryString += queryObj;
        }

        // and if all is OK return the string without the last "&" character, so it stays a valid
        // GET query string
        if (queryString.length() > 1) {
            return queryString.substring(0, queryString.length() - 1);
        } else {
            return queryString;
        }
    }
}
