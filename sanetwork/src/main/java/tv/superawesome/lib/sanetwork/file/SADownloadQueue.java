package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabriel.coman on 03/10/16.
 */
public class SADownloadQueue {

    private List<SADownloadItem> queue = null;

    public SADownloadQueue () {
        queue = new ArrayList<>();
    }

    public void addToQueue (SADownloadItem item) {
        queue.add(item);
    }

    public void removeFromQueue (SADownloadItem item) {
        queue.remove(item);
    }

    public int getLength () {
        return queue.size();
    }

    public boolean hasItemForURL (String url) {

        for (SADownloadItem item : queue) {
            if (item.urlKey.equals(url)) {
                return true;
            }
        }

        return false;
    }

    public SADownloadItem itemForURL (String url) {

        for (SADownloadItem item : queue) {
            if (item.urlKey.equals(url)) {
                return item;
            }
        }

        return null;
    }

    public SADownloadItem getNext () {

        for (SADownloadItem item : queue) {
            if (!item.isOnDisk) {
                return item;
            }
        }

        return null;

    }

}
