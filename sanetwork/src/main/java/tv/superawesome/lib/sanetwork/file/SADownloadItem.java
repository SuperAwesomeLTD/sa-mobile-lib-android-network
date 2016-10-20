package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gabriel.coman on 03/10/16.
 */
public class SADownloadItem {

    private static final String SA_KEY_PREFIX = "sasdkkey_";
    private static final short MAX_RETRIES = 3;

    public String urlKey = null;
    public String key = null;
    public String diskName = null;
    public String diskUrl = null;
    public boolean isOnDisk = false;
    public int nrRetries = 0;
    public List<SAFileDownloaderInterface> responses = null;

    /**
     * Base constructor
     */
    public SADownloadItem () {
        responses = new ArrayList<>();
    }

    /**
     * Constructor w/ URL that also does disk & key setup
     * @param url a remote resource URL
     */
    public SADownloadItem (String url) {
        // call to first constructor
        this ();

        // get the URL Key
        urlKey = url;

        // get the disk name
        if (urlKey != null) {
            diskName = getNewDiskName(url);
            diskUrl = diskName;
            key = getKeyForDiskName(diskName);
        }
    }

    /**
     * Constructor w/ URL that also does disk & key setup and also adds the first response
     * interface to the responses array
     * @param url a remote resource URL
     * @param firstInterface a listener for callback
     */
    public SADownloadItem (String url, SAFileDownloaderInterface firstInterface) {
        // call URL constructor
        this (url);

        // add response
        addResponse(firstInterface);
    }


    /**
     * Increment the nr of retries this download item can use
     */
    public void incrementNrRetries () {
        nrRetries++;
    }

    /**
     * Check to see if the retries condition is implemented
     * @return true or false depending on the condition
     */
    public boolean hasRetriesRemaining () {
        return nrRetries < MAX_RETRIES;
    }

    /**
     * Clear the responses array
     */
    public void clearResponses () {
        responses.clear();
    }

    /**
     * Add a new response to the responses array (if not null)
     * @param listener a new listener for callback
     */
    public void addResponse (SAFileDownloaderInterface listener) {
        if (listener != null) {
            responses.add(listener);
        }
    }

    /**
     * Determines if the current download item is valid
     * @return true or false
     */
    public boolean isValid () {
        return urlKey != null && diskName != null && diskUrl != null && key != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // File path methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the file extension from a file name
     * @param fileName a valid, hopefully not-null filename
     * @return the 3-4 letter extension of the file
     */
    public String getFileExt(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        } else {
            return null;
        }
    }

    /**
     * Generate a new disk name based on an url's extension
     * @param url url to pass to get the extension from
     * @return a new disk name
     */
    public String getNewDiskName (String url) {
        if (url != null && !url.isEmpty()) {
            String extension = getFileExt(url);
            if (extension != null && !extension.isEmpty()) {
                return "samov_" + new Random().nextInt(65548) + "." + extension;
            } else {
                return null;
            }
        }else {
            return null;
        }
    }

    /**
     * Get a key from a disk name
     * @param diskName valid disk name
     * @return a new key
     */
    public String getKeyForDiskName (String diskName) {
        if (diskName != null && !diskName.isEmpty()) {
            return SA_KEY_PREFIX + "_" + diskName;
        } else {
            return null;
        }
    }
}
