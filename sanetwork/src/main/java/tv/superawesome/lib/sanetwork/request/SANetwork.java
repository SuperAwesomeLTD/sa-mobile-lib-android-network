/**
 * Copyright:   SuperAwesome Trading Limited 2017
 * Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.request;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTask;
import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTaskInterface;

/**
 * This is the main class that abstracts away most major network operations needed in order
 * to communicate with the ad server
 */
public class SANetwork {

    /**
     * This is a sister method to the private "sendRequest" method that will execute a GET
     * HTTP request
     */
    public void sendGET(Context context, String url, JSONObject query, JSONObject header, SANetworkInterface listener) {
        sendRequest(context, url, "GET", query, header, new JSONObject(), listener);
    }

    /**
     * This is a sister method to the private "sendRequest" method that will execute a POST
     * HTTP request
     */
    public void sendPOST(Context context, String url, JSONObject query, JSONObject header, JSONObject body, SANetworkInterface listener) {
        sendRequest(context, url, "POST", query, header, body, listener);
    }


    /**
     * This is a sister method to the private "sendRequest" method that will execute a PUT
     * HTTP request
     */
    public void sendPUT(Context context, String url, JSONObject query, JSONObject header, JSONObject body, SANetworkInterface listener) {
        sendRequest(context, url, "PUT", query, header, body, listener);
    }

    /**
     * This is the generic request method.
     * It abstracts away the standard Android HttpUrlConnection code and wraps it in an
     * async task, in order to be easily executable anywhere.
     * This method does not get exposed to the public; Rather, sister methods like sendPUT,
     * sendGET, etc, will be presented as public.
     *
     * @param context   the current context (an Activity or Fragment, etc)
     * @param url       URL to send the request to
     * @param method    the HTTP method to be executed, as a string. Based on the methods possible
     *                  with the HttpsURLConnection class (OPTIONS, GET, HEAD, POST, PUT,
     *                  DELETE and TRACE)
     * @param query     a JSON object containing all the query parameters to be added to an URL
     *                  (mostly for a GET type request)
     * @param header    a JSON object containing all the header parameters to be added
     *                  to the request
     * @param body      a JSON object containing all the body parameters to be added to
     *                  a PUT or POST request
     * @param listener  a listener of type SANetworkInterface to be used as a callback mechanism
     *                  when the network operation finally succeeds
     */
    private void sendRequest(Context context, String url, final String method, JSONObject query, final JSONObject header, final JSONObject body, final SANetworkInterface listener) {

        // create the final endpoint to hit
        // it will be formed by taking the original URL that's being passed and any existing
        // query parameters (from the "query" JSONObject method parameter)
        final String endpoint = url + (!isJSONEmpty(query) ? "?" + formGetQueryFromDict(query) : "");

        // create an async task that will run all the network code
        SAAsyncTask task = new SAAsyncTask(context, new SAAsyncTaskInterface() {
            @Override
            public Object taskToExecute() throws Exception {

                int statusCode;
                String response;
                InputStreamReader in;
                OutputStream os = null;

                // create a new URL object from the final endpoint that's being supplied
                URL Url = new URL(endpoint);

                // ang get the protocol (hopefully it being HTTPS or HTTP)
                String proto = Url.getProtocol();

                //
                // Case 1: Protocol is HTTPS
                if (proto.equals("https")) {

                    // create a new HTTPS Connection
                    HttpsURLConnection conn = (HttpsURLConnection)Url.openConnection();

                    // set connection parameters
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setRequestMethod(method);
                    // and in the POST & PUT cases, make sure I can write to the request as well
                    if (method.equals("POST") || method.equals("PUT")) {
                        conn.setDoOutput(true);
                    }

                    // set headers
                    if (header != null) {
                        Iterator<String> keys = header.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = header.optString(key);
                            conn.setRequestProperty(key, value);
                        }
                    }

                    // once the headers have been set, finally open the connection
                    conn.connect();

                    // if it's POST & PUT, also write any existing found body
                    if (body != null && (method.equals("POST") || method.equals("PUT"))) {
                        String message = body.toString();
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                    }

                    // read the result
                    // error cases are based on HTTP status codes greater than 400
                    statusCode = conn.getResponseCode();
                    if(statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                        in = new InputStreamReader(conn.getErrorStream());
                    } else {
                        in = new InputStreamReader(conn.getInputStream());
                    }

                    // read the response from the server
                    String line;
                    response = "";
                    BufferedReader reader = new BufferedReader(in);
                    while ((line = reader.readLine()) != null) {
                        response+=line;
                    }

                    // close the body writer
                    if (os != null) {
                        os.close();
                    }

                    // close the reader
                    in.close();

                    // disconnect
                    conn.disconnect();
                }
                //
                // Case 2: Protocol is hopefully HTTP (or any other, in a worse case scenario)
                else {
                    // create a new HTTPS Connection
                    HttpURLConnection conn = (HttpURLConnection)Url.openConnection();

                    // set connection parameters
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setRequestMethod(method);
                    // and in the POST & PUT cases, make sure I can write to the request as well
                    if (method.equals("POST") || method.equals("PUT")) {
                        conn.setDoOutput(true);
                    }

                    // set headers
                    if (header != null) {
                        Iterator<String> keys = header.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = header.optString(key);
                            conn.setRequestProperty(key, value);
                        }
                    }

                    // once the headers have been set, finally open the connection
                    conn.connect();

                    // if it's POST & PUT, also write any existing found body
                    if (body != null && (method.equals("POST") || method.equals("PUT"))) {
                        String message = body.toString();
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                    }

                    // read the result
                    // error cases are based on HTTP status codes greater than 400
                    statusCode = conn.getResponseCode();
                    if(statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                        in = new InputStreamReader(conn.getErrorStream());
                    } else {
                        in = new InputStreamReader(conn.getInputStream());
                    }

                    // read the response from the server
                    String line;
                    response = "";
                    BufferedReader reader = new BufferedReader(in);
                    while ((line = reader.readLine()) != null) {
                        response+=line;
                    }

                    // close the body writer
                    if (os != null) {
                        os.close();
                    }

                    // close the reader
                    in.close();

                    // disconnect
                    conn.disconnect();
                }

                // The final response of the SAAsyncTask will be a hash map containing the
                // current status code and any eventual payload, as a string.
                HashMap<String, Object> networkResponse = new HashMap<>();
                networkResponse.put("statusCode", statusCode);
                networkResponse.put("payload", response);

                // return the previous hash map
                return networkResponse;
            }

            @Override
            public void onFinish(Object result) {

                Log.d("SuperAwesome", "Result is " + result);
                if (result != null && result instanceof HashMap) {

                    // get the hash map
                    HashMap<String, Object> response = (HashMap<String, Object>)result;
                    int status = -1;
                    String payload = null;
                    if (response.containsKey("statusCode")) {
                        status = (int) response.get("statusCode");
                    }
                    if (response.containsKey("payload")) {
                        payload = (String) response.get("payload");
                    }

                    // call the result
                    if (status > -1 && payload != null) {
                        if (listener != null) {
                            Log.d("SuperAwesome", "[true] | HTTP " + method + " | " + status + " | " + endpoint + " ==> " + payload);
                            listener.response(status, payload, true);
                        }
                    } else {
                        if (listener != null) {
                            Log.d("SuperAwesome", "[false] | HTTP " + method + " | " + status + " | " + endpoint);
                            listener.response(0, null, false);
                        }
                    }
                } else {
                    if (listener != null) {
                        Log.d("SuperAwesome", "[false] | HTTP " + method + " | " + 0 + " | " + endpoint);
                        listener.response(0, null, false);
                    }
                }
            }

            @Override
            public void onError() {
                Log.d("SuperAwesome", "[false] | HTTP " + method + " | " + 0 + " | " + endpoint);
                if (listener != null) {
                    listener.response(0, null, false);
                }
            }
        });
    }

    /**
     * This method checks all possibilities to determine if a passed JSONObject is null or empty.
     *
     * @param dict  a JSONObject that will be checked for emptiness / validity
     * @return      either true or false, if conditions are met
     */
    private static boolean isJSONEmpty(JSONObject dict) {
        return dict == null || dict.length() == 0 || dict.toString().equals("{}");
    }

    /**
     * This method takes a JSONObject paramter and returns it as a valid GET query string
     * (e.g. a JSON { "name": "John", "age": 23 } would become "name=John&age=23"
     *
     * @param dict  a JSON object to be transformed into a GET query string
     * @return      a valid GET query string
     */
    private static String formGetQueryFromDict(JSONObject dict) {
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
