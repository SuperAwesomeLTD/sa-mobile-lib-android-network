package tv.superawesome.lib.sanetwork.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Set;

import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTask;
import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTaskInterface;

/**
 * Created by gabriel.coman on 03/10/16.
 */
public class SAFileDownloader {

    // constants
    private final String PREFERENCES = "MyPreferences";

    private SADownloadQueue queue = null;
    private SADownloadItem currentItem = null;
    private boolean isDownloaderBusy;
    private boolean cleanupOnce = false;
    private boolean printStart = false;
    private boolean printQuarter = false;
    private boolean printMid = false;
    private boolean printThird = false;
    private boolean printFull =false;

    // Singleton definition
    private static SAFileDownloader instance = new SAFileDownloader();

    /**
     * Private constructor
     */
    private SAFileDownloader() {

        // init the queue
        queue = new SADownloadQueue();

        // init the current item (with null)
        currentItem = null;

        // at start, downloader is not busy
        isDownloaderBusy = false;
    }

    /**
     * Public instance method
     * @return returns the only singleton instance
     */
    public static SAFileDownloader getInstance() {
        return instance;
    }

    /**
     * Download a file from a remote location to the disk
     * @param url the remote url
     * @param listener the callback when the async call succeeds
     */
    public void downloadFileFrom(Context context, String url, SAFileDownloaderInterface listener) {

        // check for null context
        if (context == null) {
            if (listener != null) {
                listener.response(false, null);
            }
            return;
        }

        // cleanup the disk cache once!
        if (!cleanupOnce) {
            cleanupOnce = true;
            cleanup(context);
        }

        // if File is already in queue
        if (queue.hasItemForURL(url)) {

            Log.d("SuperAwesome", "URL already exists in queue: " + url);

            // get item
            SADownloadItem item = queue.itemForURL(url);

            // get status
            boolean isOnDisk = item != null && item.isOnDisk;

            // if file is already downloaded & exists
            if (isOnDisk) {
                if (listener != null) {
                    listener.response(true, item.diskUrl);
                }
            }
            // if file not already downloaded add to queue
            else {
                if (item != null) {
                    item.addResponse(listener);
                }
            }

        }
        // if File is not already in queue
        else {

            // create a new item
            SADownloadItem item = new SADownloadItem(url, listener);

            // if the new item is valid (e.g. valid url, disk path, key, etc)
            // then proceed with the operation
            if (item.isValid()) {
                Log.d("SuperAwesome", "Adding new URL to queue: " + url);

                // add the item to the queue
                queue.addToQueue(item);

                // check on queue
                checkOnQueue(context);
            }
            // if it's not ok (e.g. invalid url) then respond w/ false
            else {
                Log.d("SuperAwesome", "Not adding new URL to queue: " + url + " becase it's not valid");

                if (listener != null) {
                    listener.response(false, null);
                }
            }
        }
    }

    private void  checkOnQueue (final Context context) {

        // start downloader if it's not busy & queue still has something
        if (!isDownloaderBusy && queue.getLength() > 0) {

            // assign "next" item to current item
            currentItem = queue.getNext();

            // if that current "next" item actually exists
            if (currentItem != null) {

                // if this item can actually be downloaded (e.g. nr retries < MAX)
                if (currentItem.hasRetriesRemaining()) {

                    Log.d("SuperAwesome", "Start work on queue for " + currentItem.diskUrl + " Try " + (currentItem.nrRetries + 1) + " / 3");

                    // reset these state vars to handle state
                    isDownloaderBusy = true;
                    printStart = printQuarter = printMid = printThird = printFull = false;

                    SAAsyncTask task = new SAAsyncTask(context, new SAAsyncTaskInterface() {
                        @Override
                        public Object taskToExecute() throws Exception {

                            // get the original SA unique key
                            String filename = currentItem.diskUrl;
                            String videoUrl = currentItem.urlKey;

                            // success var
                            boolean success = true;

                            // create streams
                            InputStream input = null;
                            OutputStream output = null;
                            HttpURLConnection connection = null;

                            try {
                                // start connection
                                URL url = new URL(videoUrl);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.connect();

                                int statusCode = connection.getResponseCode();

                                // exception code != 200
                                if (statusCode != HttpURLConnection.HTTP_OK) return null;

                                // get input stream and start writing to disk
                                input = connection.getInputStream();
                                output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                                int file_size = connection.getContentLength();

                                byte data[] = new byte[4096];
                                long total = 0;
                                int count;
                                while ((count = input.read(data)) != -1) {
                                    total += count;

                                    // print
                                    int percent = (int) ((total / (float) file_size) * 100);
                                    if (percent >= 0 && !printStart) {
                                        printStart = true;
                                        Log.d("SuperAwesome", "Wrote " + percent + " %");
                                    }
                                    if (percent >= 25 && !printQuarter) {
                                        printQuarter = true;
                                        Log.d("SuperAwesome", "Wrote " + percent + " %");
                                    }
                                    if (percent >= 50 && !printMid) {
                                        printMid = true;
                                        Log.d("SuperAwesome", "Wrote " + percent + " %");
                                    }
                                    if (percent >= 75 && !printThird) {
                                        printThird = true;
                                        Log.d("SuperAwesome", "Wrote " + percent + " %");
                                    }
                                    if (percent >= 100 && !printFull) {
                                        printFull = true;
                                        Log.d("SuperAwesome", "Wrote " + percent + " %");
                                    }

                                    // actually write
                                    output.write(data, 0, count);
                                }

                            } catch (Exception e) {
                                success = false;
                            }

                            // try to close this
                            try {
                                if (output != null) output.close();
                                if (input != null) input.close();
                            } catch (IOException ignored) {}

                            // disconnect
                            if (connection != null) connection.disconnect();

                            // return
                            return success;
                        }

                        @Override
                        public void onFinish(Object result) {

                            if ((Boolean) result) {

                                Log.d("SuperAwesome", "Downloaded " + currentItem.urlKey + " ==> " + currentItem.diskUrl);

                                // put data in the editor
                                SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString(currentItem.key, currentItem.diskUrl);
                                editor.apply();

                                // send response
                                for (SAFileDownloaderInterface listener : currentItem.responses) {
                                    listener.response(true, currentItem.diskUrl);
                                }

                                // set on disk
                                currentItem.isOnDisk = true;

                                // clear responses
                                currentItem.clearResponses();

                                // downloader not busy
                                isDownloaderBusy = false;

                                // check on queue
                                checkOnQueue(context);

                            } else {
                                // handle the error case
                                isDownloaderBusy = false;
                                currentItem.incrementNrRetries();
                                currentItem.isOnDisk = false;
                                queue.moveToBackOfQueue(currentItem);
                                checkOnQueue(context);
                            }
                        }

                        @Override
                        public void onError() {
                            // handle the error case
                            isDownloaderBusy = false;
                            currentItem.incrementNrRetries();
                            currentItem.isOnDisk = false;
                            queue.moveToBackOfQueue(currentItem);
                            checkOnQueue(context);
                        }
                    });

                }
                // if not, then renounce downloading it
                else {

                    // send error events
                    for (SAFileDownloaderInterface listener : currentItem.responses) {
                        listener.response(false, null);
                    }

                    // clear responses
                    currentItem.clearResponses();

                    // remove from queue
                    queue.removeFromQueue(currentItem);

                    // check again
                    checkOnQueue(context);
                }
            }
        }
    }

    /**
     * Cleanup function - it will remove files and reset preferences
     */
    private void cleanup(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> keys = preferences.getAll().keySet();
        for (String key : keys) {

            try {

                String filename = preferences.getString(key, null);

                if (filename != null) {
                    String fullPath = context.getFilesDir() + "/" + filename;
                    File file = new File(context.getFilesDir(), filename);
                    boolean hasBeenDeleted = false;
                    if (file.exists()) {
                        hasBeenDeleted = file.delete();
                    }
                    if (hasBeenDeleted) {
                        Log.d("SuperAwesome", "[true] | DEL | " + fullPath);
                    } else {
                        Log.d("SuperAwesome", "[false] | DEL | " + fullPath);
                    }
                    editor.remove(key);
                    editor.apply();
                }

            } catch (ClassCastException e) {
                // do nothing
            }
        }

        // apply
        editor.apply();
    }
}
