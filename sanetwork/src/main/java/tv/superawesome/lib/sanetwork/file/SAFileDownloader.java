/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class abstracts away the details of downloading files through a queue.
 * The main purpose is for class users to add files to be downloaded on the queue and then
 * for it to proceed to downloaded them one at a time.
 *
 * This is very useful when downloading large video files off the network, for example.
 *
 */
public class SAFileDownloader {

    // constants
    private final String PREFERENCES = "MyPreferences";

    // private members needed to download a file
    private boolean cleanupOnce = false;

    // Executor
    private int timeout = 15000;
    private boolean isDebug = false;
    private Executor executor = null;

    /**
     * Classic constructor
     */
    public SAFileDownloader() {
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Other singleton, with an executor passes as param
     * @param executor executor to override
     * @return the only instance of the SAFileDownloader object
     */
    public SAFileDownloader(Executor executor, boolean isDebug, int timeout) {
        this.executor = executor;
        this.isDebug = isDebug;
        this.timeout = timeout;
    }

    /**
     * This is the class's only public method - and it allows users to add URLs to a queue of
     * downloading items. It will then know how to download them one after another so as not to
     * cause too much strain on network resources.
     *
     * @param url       The remote URL from where to get a certain file
     * @param listener1 instance of the SAFileDownloaderInterface interface, which acts as a
     *                  callback to the main thread for this method
     */
    public void downloadFileFrom(final Context context, final String url, SAFileDownloaderInterface listener1) {

        // get a local copy of the listener
        final SAFileDownloaderInterface listener = listener1 != null ? listener1 : new SAFileDownloaderInterface() {@Override public void saDidDownloadFile(boolean success, String diskUrl) {}};

        // check for null context
        if (context == null) {
            listener.saDidDownloadFile(false, null);
            return;
        }

        // cleanup the disk cache once!
        if (!cleanupOnce && !isDebug) {
            cleanupOnce = true;
            cleanup(context);
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {

                final SAFileItem currentItem = new SAFileItem(url);

                // get the disk URL and unique URL Key
                String filename = currentItem.getDiskUrl();
                String videoUrl = currentItem.getUrlKey();

                // current success var (that's to be returned)
                boolean success = true;

                // create streams
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;

                try {
                    // start a new Http connection)
                    URL url = new URL(videoUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(timeout);
                    connection.setConnectTimeout(timeout);
                    connection.connect();

                    int statusCode = connection.getResponseCode();

                    // exception code != 200
                    if (statusCode != HttpURLConnection.HTTP_OK) return;

                    // get input stream and start writing to disk
                    input = connection.getInputStream();
                    output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                    int file_size = connection.getContentLength();

                    // start the file download operation
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        int percent = (int) ((total / (float) file_size) * 100);

                        if (!isDebug && (percent % 25 == 0)) {
                            Log.d("SuperAwesome", "Have written " +  percent + "% of file");
                        }

                        // actually write the data to the disk
                        output.write(data, 0, count);
                    }

                } catch (Exception e) {
                    success = false;
                }

                // try to close the whole connection
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                    // ignore
                }

                // disconnect
                if (connection != null) connection.disconnect();

                if (success) {

                    // put data in the editor
                    SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                    preferences.edit().putString(currentItem.getKey(), currentItem.getDiskUrl()).commit();

                    // send back
                    sendBack(listener, true, currentItem.getDiskUrl());
                }
                else {
                    sendBack(listener, false, null);
                }
            }
        });
    }

    private void sendBack (final SAFileDownloaderInterface listener, final boolean success, final String diskUrl) {
        /**
         * And try to return it on the main thread
         */
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.saDidDownloadFile(success, diskUrl);
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
                listener.saDidDownloadFile(success, diskUrl);
            }
        }
    }

    /**
     * This method is used to cleanup all existing files in the Android "filesDir" that may have
     * been downloaded in a previous session. This is useful so as to not end up with a lot of
     * space being wasted on the user's device.
     *
     * @param context the current context (activity or fragment)
     */
    private void cleanup (Context context) {

        // get current preferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        // run through the whole key set and try to delete existing files
        for (String key : preferences.getAll().keySet()) {
            try {

                // get the current filename
                String filename = preferences.getString(key, null);

                // and if it exists, delete it
                if (filename != null) {
                    String fullPath = context.getFilesDir() + "/" + filename;
                    File file = new File(context.getFilesDir(), filename);
                    boolean hasBeenDeleted = false;
                    if (file.exists()) {
                        hasBeenDeleted = file.delete();
                        if (!isDebug) {
                            Log.d("SuperAwesome", "Deleted " + filename);
                        }
                    }

                    // remove the key from the shared preferences as well
                    preferences.edit().remove(key).apply();
                }

            } catch (ClassCastException e) {
                // do nothing
            }
        }

        // apply
        preferences.edit().apply();
    }
}
