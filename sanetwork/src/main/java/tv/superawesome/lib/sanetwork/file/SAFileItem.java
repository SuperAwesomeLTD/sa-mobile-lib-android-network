/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

/**
 * This class represents a single File Item - an object that tries to group two pieces of
 * information:
 *  - the details of where a file is downloaded (and if it has been successfully downloaded)
 *  - all the possible 3rd parties that would be interested in knowing if the file has been
 *  downloaded (by using a List of SAFileDownloaderInterfaces to keep a track of who needs to be
 *  notified)
 */
public class SAFileItem {

    // private constants
    private static final String SA_KEY_PREFIX = "sasdkkey_";

    // private member functions
    private URL resourceURL = null;
    private String urlKey = null;
    private String key = null;
    private String diskName = null;
    private String diskUrl = null;

    /**
     * Empty Item constructor
     */
    public SAFileItem() {
        // do nothing
    }

    /**
     * Constructor that takes a single URL parameter and from there creates the
     * associated disk name, disk url and key
     *
     * @param url   a remote resource URL
     */
    public SAFileItem(String url) {
        // get the URL Key
        urlKey = url;

        try {
            resourceURL = new URL(url);
            diskName = fileNameOf(url);
            key = getKeyForDiskName(diskName);
            diskUrl = diskName;
        } catch (Exception e) {
            // do nothing
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
     * Setter for the url key member var
     *
     * @param urlKey new value to override
     */
    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
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

    private String fileNameOf (String url) {
        try {
            URI uri = new URI(url);
            String[] segments = uri.getPath().split("/");
            return segments[segments.length-1];
        } catch (Exception e) {
            return null;
        }
    }

    public URL getResourceURL() {
        return resourceURL;
    }
}
