/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.request;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is the main class that abstracts away most major network operations needed in order
 * to communicate with the ad server
 */
public class SANetwork {

    private int timeout = 15000;
    private Executor executor = null;
    private SANetworkUtils utils = new SANetworkUtils();

    /**
     * Constructor without any executor, so choose a new single thread executor
     */
    public SANetwork () {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Constructor with executor
     * @param executor - the executor that may be passed in as param
     */
    public SANetwork (Executor executor, int timeout) {
        this.executor = executor;
        this.timeout = timeout;
    }

    /**
     * This is a sister method to the private "sendRequest" method that will execute a GET
     * HTTP request
     */
    public void sendGET(String url, JSONObject query, JSONObject header, SANetworkInterface listener) {
        sendRequest(url, "GET", query, header, new JSONObject(), listener);
    }

    /**
     * This is a sister method to the private "sendRequest" method that will execute a POST
     * HTTP request
     */
    public void sendPOST(String url, JSONObject query, JSONObject header, JSONObject body, SANetworkInterface listener) {
        sendRequest(url, "POST", query, header, body, listener);
    }


    /**
     * This is a sister method to the private "sendRequest" method that will execute a PUT
     * HTTP request
     */
    public void sendPUT(String url, JSONObject query, JSONObject header, JSONObject body, SANetworkInterface listener) {
        sendRequest(url, "PUT", query, header, body, listener);
    }

    /**
     * This is the generic request method.
     * It abstracts away the standard Android HttpUrlConnection code and wraps it in an
     * async task, in order to be easily executable anywhere.
     * This method does not get exposed to the public; Rather, sister methods like sendPUT,
     * sendGET, etc, will be presented as public.
     *
     * @param endpoint  URL to send the request to
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
    private void sendRequest(final String endpoint,
                             final String method,
                             final JSONObject query,
                             final JSONObject header,
                             final JSONObject body,
                             final SANetworkInterface listener) {

        executor.execute(new Runnable() {
            @Override
            public void run() {

                final String finalEndpoint = endpoint + (!utils.isJSONEmpty(query) ? "?" + utils.formGetQueryFromDict(query) : "");

                try {
                    int statusCode;
                    String response;
                    InputStreamReader in;
                    OutputStream os = null;

                    // create a new URL object from the final endpoint that's being supplied
                    URL Url = new URL(finalEndpoint);

                    // ang get the protocol (hopefully it being HTTPS or HTTP)
                    String proto = Url.getProtocol();

                    //
                    // Case 1: Protocol is HTTPS
                    if (proto.equals("https")) {

                        // create a new HTTPS Connection
                        HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();

                        // set connection parameters
                        conn.setReadTimeout(timeout);
                        conn.setConnectTimeout(timeout);
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
                        if (statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                            in = new InputStreamReader(conn.getErrorStream());
                        } else {
                            in = new InputStreamReader(conn.getInputStream());
                        }

                        // read the saDidGetResponse from the server
                        String line;
                        response = "";
                        BufferedReader reader = new BufferedReader(in);
                        while ((line = reader.readLine()) != null) {
                            response += line;
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
                        HttpURLConnection conn = (HttpURLConnection) Url.openConnection();

                        // set connection parameters
                        conn.setReadTimeout(timeout);
                        conn.setConnectTimeout(timeout);
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
                        if (statusCode >= HttpsURLConnection.HTTP_BAD_REQUEST) {
                            in = new InputStreamReader(conn.getErrorStream());
                        } else {
                            in = new InputStreamReader(conn.getInputStream());
                        }

                        // read the saDidGetResponse from the server
                        String line;
                        response = "";
                        BufferedReader reader = new BufferedReader(in);
                        while ((line = reader.readLine()) != null) {
                            response += line;
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

                    if (statusCode < HttpsURLConnection.HTTP_BAD_REQUEST && response != null) {
                        sendBack(listener, statusCode, response, true);
                    }
                    else {
                        sendBack(listener, statusCode, null, false);
                    }

                } catch (Exception e) {
                    sendBack(listener, 0, null, false);
                }
            }
        });
    }

    private void sendBack (final SANetworkInterface listener, final int status, final String response, final boolean success) {
        /**
         * And try to return it on the main thread
         */
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.saDidGetResponse(status, response, success);
                    }
                }
            });
        }
        /**
         * If the Main Looper is not present, as in a testing environment, still
         * return the callback, but on the same thread.
         */
        catch (Exception e) {
            if (listener != null) {
                listener.saDidGetResponse(status, response, success);
            }
        }
    }
}