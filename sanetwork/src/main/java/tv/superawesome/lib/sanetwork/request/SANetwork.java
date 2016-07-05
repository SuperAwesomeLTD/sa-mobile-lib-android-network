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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import tv.superawesome.lib.sanetwork.asynctask.*;

/**
 * Created by gabriel.coman on 06/04/16.
 */
public class SANetwork {

    /**
     * GET function
     * @param context context
     * @param url url to send GET request to
     * @param query the query
     * @param header the header
     * @param listener the listener for response
     */
    public void sendGET(Context context, String url, JSONObject query, JSONObject header, SANetworkInterface listener) {
        sendRequest(context, url, "GET", query, header, new JSONObject(), listener);
    }

    /**
     * POST function
     * @param context the context
     * @param url the URL
     * @param query the query
     * @param header the header
     * @param body the body
     * @param listener the listener
     */
    public void sendPOST(Context context, String url, JSONObject query, JSONObject header, JSONObject body, SANetworkInterface listener) {
        sendRequest(context, url, "POST", query, header, body, listener);
    }

    /**
     * Request function
     * @param context current context
     * @param url URL to send the request to
     * @param method method (POST or GET)
     * @param query query parameters
     * @param header header parameters
     * @param body body parameters
     * @param listener interface listener to get responses on
     */
    private void sendRequest(Context context, String url, final String method, JSONObject query, final JSONObject header, final JSONObject body, final SANetworkInterface listener) {

        // endpoint
        final String endpoint = url + (!isJSONEmpty(query) ? "?" + formGetQueryFromDict(query) : "");

        SAAsyncTask task = new SAAsyncTask(context, new SAAsyncTaskInterface() {
            @Override
            public Object taskToExecute() throws Exception {

                int statusCode;
                String response;
                InputStreamReader in;
                OutputStream os = null;

                // get the Url
                URL Url = new URL(endpoint);
                String proto = Url.getProtocol();

                if (proto.equals("https")) {
                    HttpsURLConnection conn = (HttpsURLConnection)Url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setRequestMethod(method);

                    // set headers
                    Iterator<String> keys = header.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = header.optString(key);
                        conn.setRequestProperty(key, value);
                    }

                    // connect
                    conn.connect();

                    // write body
                    if (method.equals("POST")) {
                        conn.setDoOutput(true);
                        String message = body.toString();
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                    }

                    // read the result
                    statusCode = conn.getResponseCode();
                    if(statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                        in = new InputStreamReader(conn.getErrorStream());
                    } else {
                        in = new InputStreamReader(conn.getInputStream());
                    }

                    // get response
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
                } else {
                    HttpURLConnection conn = (HttpURLConnection)Url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setRequestMethod(method);

                    // set headers
                    Iterator<String> keys = header.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = header.optString(key);
                        conn.setRequestProperty(key, value);
                    }

                    // connect
                    conn.connect();

                    // write body
                    if (method.equals("POST")) {
                        conn.setDoOutput(true);
                        String message = body.toString();
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                    }

                    // read the result
                    statusCode = conn.getResponseCode();
                    if(statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                        in = new InputStreamReader(conn.getErrorStream());
                    } else {
                        in = new InputStreamReader(conn.getInputStream());
                    }

                    // get response
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

                // return
                return new SANetworkResponse(statusCode, response);
            }

            @Override
            public void onFinish(Object result) {
                if (result != null) {
                    SANetworkResponse response = (SANetworkResponse)result;
                    listener.success(response.status, response.payload);
                } else {
                    listener.failure();
                }
            }

            @Override
            public void onError() {
                listener.failure();
            }
        });
    }

    /**
     * Checks if an JSON is empty
     * @param dict the JSON object
     * @return either true or false
     */
    private static boolean isJSONEmpty(JSONObject dict) {
        if (dict == null) return true;
        if (dict.length() == 0) return true;
        if (dict.toString().equals("{}")) return true;
        return false;
    }

    /**
     * Form a GET Query from a JSON object
     * @param dict the JSON object
     * @return a String with a query
     */
    private static String formGetQueryFromDict(JSONObject dict) {
        String queryString = "";

        ArrayList<String> queryArray = new ArrayList<>();
        Iterator<String> keys = dict.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object valObj = dict.opt(key);
            String val = (valObj != null ? valObj.toString().replace("\"","") : "");

            queryArray.add(key + "=" + val + "&");
        }

        for (String queryObj : queryArray) {
            queryString += queryObj;
        }

        if (queryString.length() > 1) {
            return queryString.substring(0, queryString.length() - 1);
        } else {
            return queryString;
        }
    }
}
