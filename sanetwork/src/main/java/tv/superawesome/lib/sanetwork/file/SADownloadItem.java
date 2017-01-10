/**
 * Copyright:   SuperAwesome Trading Limited 2017
 * Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents a single Download Item - an object that tries to group two pieces of
 * information:
 *  - the details of where a file is downloaded (and if it has been successfully downloaded)
 *  - all the possible 3rd parties that would be interested in knowing if the file has been
 *  downloaded (by using a List of SAFileDownloaderInterfaces to keep a track of who needs to be
 *  notified)
 */
public class SADownloadItem {

    // private constants
    private static final String SA_KEY_PREFIX = "sasdkkey_";
    private static final short MAX_RETRIES = 3;

    // private member functions
    private String urlKey = null;
    private String key = null;
    private String diskName = null;
    private String diskUrl = null;
    private boolean isOnDisk = false;
    private int nrRetries = 0;
    private List<SAFileDownloaderInterface> responses = new ArrayList<>();

    /**
     * Empty Item constructor
     */
    public SADownloadItem () {
        // do nothing
    }

    /**
     * Constructor that takes a single URL parameter and from there creates the
     * associated disk name, disk url and key
     *
     * @param url   a remote resource URL
     */
    public SADownloadItem (String url) {
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
     * Constructor that takes a single URL parameter and from there creates the
     * associated disk name, disk url and key and also adds the first listener to the responses
     * array
     *
     * @param url           a remote resource URL
     * @param firstListener a first listener used for callback
     */
    public SADownloadItem (String url, SAFileDownloaderInterface firstListener) {
        // call URL constructor
        this (url);

        // add response
        addResponse(firstListener);
    }


    /**
     * Increment the nr of retries this download item can use
     */
    public void incrementNrRetries () {
        nrRetries++;
    }

    /**
     * Check to see if the retries condition is implemented
     *
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
     *
     * @param listener add a new listener for callback, only if its non-null
     */
    public void addResponse (SAFileDownloaderInterface listener) {
        if (listener != null) {
            responses.add(listener);
        }
    }

    /**
     * Determines if the current download item is valid
     *
     * @return true or false based on the condition
     */
    public boolean isValid () {
        return urlKey != null && diskName != null && diskUrl != null && key != null;
    }

    /**
     * Get the file extension from a file name
     *
     * @param fileName a valid, hopefully not-null filename
     * @return the 3-4 letter extension of the file
     */
    private String getFileExt(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        } else {
            return null;
        }
    }

    /**
     * Generate a new disk name based on an url's extension
     *
     * @param url url to pass to get the extension from
     * @return a new disk name
     */
    private String getNewDiskName(String url) {
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
     *
     * @param diskName valid disk name
     * @return a new key
     */
    private String getKeyForDiskName(String diskName) {
        if (diskName != null && !diskName.isEmpty()) {
            return SA_KEY_PREFIX + "_" + diskName;
        } else {
            return null;
        }
    }

    /**
     * Setter for isOnDisk member var
     *
     * @param onDisk new value to override
     */
    public void setOnDisk(boolean onDisk) {
        isOnDisk = onDisk;
    }

    /**
     * Setter for the url key member var
     *
     * @param urlKey new value to override
     */
    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    /**
     * Setter for the key parameter
     *
     * @param key new value to override
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Setter for the disk name
     *
     * @param diskName new value to override
     */
    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    /**
     * Setter for the disk url
     *
     * @param diskUrl new value to override
     */
    public void setDiskUrl(String diskUrl) {
        this.diskUrl = diskUrl;
    }

    /**
     * Getter for the responses (listeners) array
     *
     * @return the whole responses array as a List of SAFileDownloaderInterface
     */
    public List<SAFileDownloaderInterface> getResponses() {
        return responses;
    }

    /**
     * Get the current nr of retries for this download item
     *
     * @return the current state of the "nrRetries" member variable
     */
    public int getNrRetries() {
        return nrRetries;
    }

    /**
     * Get the current status of the file being on disk
     *
     * @return the current state of the "isOnDisk" member variable
     */
    public boolean isOnDisk() {
        return isOnDisk;
    }

    /**
     * Get the current disk url
     *
     * @return the current state of the "diskUrl" member variable
     */
    public String getDiskUrl() {
        return diskUrl;
    }

    /**
     * Get the current disk name
     *
     * @return the current state of the "diskName" member variable
     */
    public String getDiskName() {
        return diskName;
    }

    /**
     * Get the current key
     *
     * @return the current state of the "key" member variable
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the current url key
     *
     * @return the current state of the "urlKey" member variable
     */
    public String getUrlKey() {
        return urlKey;
    }
}
