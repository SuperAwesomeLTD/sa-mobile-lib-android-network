/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

import java.net.URI;
import java.net.URL;

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
    private URL    url = null;
    private String key = null;
    private String fileName = null;
    private String filePath = null;

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
        try {
            this.url = new URL(url);
            fileName = fileNameOf(url);
            filePath = fileName;
            key = getKeyForDiskName(fileName);
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
        return url != null && fileName != null && filePath != null && key != null;
    }

    /**
     * Get a key from a disk name
     *
     * @param diskName valid disk name
     * @return a new key
     */
    private String getKeyForDiskName(String diskName) {
        return SA_KEY_PREFIX + "_" + diskName;
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

    public URL getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
