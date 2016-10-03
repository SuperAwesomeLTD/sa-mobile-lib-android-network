package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabriel.coman on 03/10/16.
 */
public class SADownloadItem {

    public String urlKey = null;
    public String key = null;
    public String diskUrl = null;
    public String ext = null;
    public boolean isOnDisk = false;
    public int nrRetries = 0;
    public List<SAFileDownloaderInterface> responses = null;

    public SADownloadItem () {
        responses = new ArrayList<>();
    }

    public void incrementNrRetries () {
        nrRetries++;
    }

    public void clearResponses () {
        responses.removeAll(responses);
    }

    public void addResponse (SAFileDownloaderInterface listener) {
        responses.add(listener);
    }

}
