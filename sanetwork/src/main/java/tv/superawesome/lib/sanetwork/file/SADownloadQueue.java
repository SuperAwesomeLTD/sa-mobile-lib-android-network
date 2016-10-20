package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabriel.coman on 03/10/16.
 */
public class SADownloadQueue {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Member vars
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<SADownloadItem> queue = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SADownloadQueue () {
        queue = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Queue operation methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addToQueue (SADownloadItem item) {
        if (item != null) {
            queue.add(item);
        }
    }

    public void removeFromQueue (SADownloadItem item) {
        if (item != null) {
            queue.remove(item);
        }
    }

    public void moveToBackOfQueue (SADownloadItem item) {
        removeFromQueue(item);
        addToQueue(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Item check & retrieve for queue
    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get the queue length
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int getLength () {
        return queue.size();
    }
}
