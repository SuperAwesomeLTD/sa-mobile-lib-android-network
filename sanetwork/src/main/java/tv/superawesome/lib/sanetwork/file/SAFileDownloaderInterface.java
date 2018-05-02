/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

/**
 * This interface is used by the file downloader to signal to the main thread that a file has
 * just been downloaded.
 */
public interface SAFileDownloaderInterface {

    /**
     * The interface's only method - that contains two callback parameters, indicating success
     * and the location on the disk where the file has been downloader
     *
     * @param success   Whether the network operation to get the file was a success
     * @param key       the saved key
     * @param filePath  The Disk URL of the recently downloaded file
     */
    void saDidDownloadFile(boolean success, String key, String filePath);

}
