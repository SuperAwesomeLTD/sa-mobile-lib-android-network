package tv.superawesome.lib.sanetwork.file;

/**
 * Created by gabriel.coman on 17/05/16.
 */
public interface SAFileDownloaderInterface {

    /**
     * Function that signals finish
     */
    void response(boolean success, String diskUrl);

}
