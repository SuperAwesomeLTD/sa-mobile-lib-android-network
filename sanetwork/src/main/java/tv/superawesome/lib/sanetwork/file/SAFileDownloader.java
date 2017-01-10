/**
 * Copyright:   SuperAwesome Trading Limited 2017
 * Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
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

import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTask;
import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTaskInterface;

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
    private SADownloadQueue queue = new SADownloadQueue();
    private SADownloadItem currentItem = null;
    private boolean isDownloaderBusy = false;
    private boolean cleanupOnce = false;
    private boolean printStart = false;
    private boolean printQuarter = false;
    private boolean printMid = false;
    private boolean printThird = false;
    private boolean printFull = false;

    // Singleton instance
    private static SAFileDownloader instance = new SAFileDownloader();

    /**
     * Main singleton instance accessor method
     *
     * @return  the only instance of the SAFileDownloader object
     */
    public static SAFileDownloader getInstance() {
        return instance;
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
    public void downloadFileFrom(Context context, String url, SAFileDownloaderInterface listener1) {

        // get a local copy of the listener
        final SAFileDownloaderInterface listener = listener1 != null ? listener1 : new SAFileDownloaderInterface() {@Override public void response(boolean success, String diskUrl) {}};

        // check for null context
        if (context == null) {
            listener.response(false, null);
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

            // get the corresponding SADownloadItem for the URL (which is the queue key)
            SADownloadItem item = queue.itemForURL(url);

            // check if it's already on disk
            boolean isOnDisk = item != null && item.isOnDisk();

            // if the file is already on disk, just use the listener to respond with a
            // successful callback (and thus save precious band with and speed up SDK
            // response times)
            if (isOnDisk) {
                listener.response(true, item.getDiskUrl());
            }
            // if file not already downloaded, add the current listener to the download item,
            // so that when it does finish downloading, this listener also gets a
            // corresponding callback
            else {
                if (item != null) {
                    item.addResponse(listener);
                }
            }

        }
        // if File is not already in queue
        else {

            // create a new download item
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
            // if it's not ok (e.g. invalid url) then use the listener to send an error callback
            else {
                Log.d("SuperAwesome", "Not adding new URL to queue: " + url + " because it's not valid");

                listener.response(false, null);
            }
        }
    }

    /**
     * This is a private method that checks on the current queue and see if either the queue is
     * ready to download something new or, if it's busy, to add the current item to be
     * downloaded to the queue for later use.
     *
     * @param context   the current context (activity or fragment)
     */
    private void  checkOnQueue (final Context context) {

        // start the downloader only if the queue is not busy and it does have something in it
        if (!isDownloaderBusy && queue.getLength() > 0) {

            // current item will become the next item in the queue
            currentItem = queue.getNext();

            // if that current "next" item actually exists
            if (currentItem != null) {

                //
                // Case 1:
                // if this item can actually be downloaded (e.g. nr retries < MAX), then proceed
                // with trying to download it
                if (currentItem.hasRetriesRemaining()) {

                    Log.d("SuperAwesome", "Start work on queue for " + currentItem.getDiskUrl() + " Try " + (currentItem.getNrRetries() + 1) + " / 3");

                    // reset these state vars to handle state
                    isDownloaderBusy = true;
                    printStart = printQuarter = printMid = printThird = printFull = false;

                    new SAAsyncTask<>(context, new SAAsyncTaskInterface<Boolean>() {
                        /**
                         * Override of the SAASyncTaks "taskToExecute" method over a Boolean
                         * generic parameter.
                         *
                         * @return              It will try to return either true or false,
                         *                      depending on the success of the file download
                         *                      operation.
                         * @throws Exception    In case of majore error it will throw a type of
                         *                      exception
                         */
                        @Override
                        public Boolean taskToExecute() throws Exception {

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
                                connection.connect();

                                int statusCode = connection.getResponseCode();

                                // exception code != 200
                                if (statusCode != HttpURLConnection.HTTP_OK) return null;

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

                                    // print operation (only for logging purposes)
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

                            // return
                            return success;
                        }

                        /**
                         * Overridden "onFinish" method of the SAAsyncTaskInterface interface.
                         *
                         * @param result    a Boolean indicating whether the network op is a
                         *                  success or not
                         */
                        @Override
                        public void onFinish(Boolean result) {

                            if (result) {

                                Log.d("SuperAwesome", "Downloaded " + currentItem.getUrlKey() + " ==> " + currentItem.getDiskUrl());

                                // put data in the editor
                                SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                                preferences.edit().putString(currentItem.getKey(), currentItem.getDiskUrl()).apply();

                                // send response to all of the listeners in the current item,
                                // so that all class users who wanted to download the same file
                                // now get their response
                                for (SAFileDownloaderInterface listener : currentItem.getResponses()) {
                                    listener.response(true, currentItem.getDiskUrl());
                                }

                                // set on disk
                                currentItem.setOnDisk(true);

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
                                currentItem.setOnDisk(false);
                                queue.moveToBackOfQueue(currentItem);
                                checkOnQueue(context);
                            }
                        }

                        /**
                         * Overridden "onError" method of the SAFileDownloaderInterface interface
                         * that will handle what happens in an error case
                         *
                         */
                        @Override
                        public void onError() {
                            // downloader isn't busy anymore
                            isDownloaderBusy = false;

                            // increment the nr of retries the downloader can do for this file
                            currentItem.incrementNrRetries();

                            // reset the "isOnDisk" boolean; it's still not OK
                            currentItem.setOnDisk(false);

                            // move the current item to the back of the queue
                            queue.moveToBackOfQueue(currentItem);

                            // continue recursing through the queue
                            checkOnQueue(context);
                        }
                    });

                }
                //
                // Case 2:
                // If the item has no more retries, usually indicative of an unavailable
                // network resource or an unconnected device, then just not do it any more
                else {

                    // send error events to all of the listeners of this download item so that
                    // every class user who wanted to download this file knows there's a
                    // problem
                    for (SAFileDownloaderInterface listener : currentItem.getResponses()) {
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
                    }
                    if (hasBeenDeleted) {
                        Log.d("SuperAwesome", "[true] | DEL | " + fullPath);
                    } else {
                        Log.d("SuperAwesome", "[false] | DEL | " + fullPath);
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
