/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.listdownload;

import java.util.List;

/**
 * An interface used by the SAFileListDownloader class
 */
public interface SAFileListDownloaderInterface {

    /**
     * When all files have been downloaded, this method will be called, which will contain
     * them in order
     *
     * @param diskLocations a List of disk locations, in the order they were downloaded
     */
    void didGetAllFiles (List<String> diskLocations);

}
