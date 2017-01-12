/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.file;

import java.util.ArrayList;
import java.util.List;

/**
 * This class operates on a list of SAFileItem objects and acts as a queue with
 * limited functionality
 */
public class SAFileQueue {

    // the private list of SADownloadItems
    private List<SAFileItem> queue = new ArrayList<>();

    /**
     * This method adds another item to the queue
     *
     * @param item a new item that's going to be checked for nullness
     */
    public void addToQueue (SAFileItem item) {
        if (item != null) {
            queue.add(item);
        }
    }

    /**
     * This method removes a specified item from the queue
     *
     * @param item the item that's going to be removed; checked for nullness
     */
    public void removeFromQueue (SAFileItem item) {
        if (item != null) {
            queue.remove(item);
        }
    }

    /**
     * This method moves an already existing item to the back of the queue by first removing the
     * item, and them adding it again
     *
     * @param item the item that's going to be moved to the back
     */
    public void moveToBackOfQueue (SAFileItem item) {
        removeFromQueue(item);
        addToQueue(item);
    }

    /**
     * Specific method that checks if there is at least one SAFileItem element in the
     * queue that corresponds to the url given as paramter
     *
     * @param url   given URL parameter
     * @return      returns true or false if at least one element is found
     */
    public boolean hasItemForURL (String url) {

        for (SAFileItem item : queue) {
            if (item.getUrlKey().equals(url)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method that returns the SAFileItem specific for a given url
     *
     * @param url   given URL parameter
     * @return      return the item or null
     */
    public SAFileItem itemForURL (String url) {

        for (SAFileItem item : queue) {
            if (item.getUrlKey().equals(url)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Method that returns the next item in queue
     *
     * @return  the next item or null, if none was found
     */
    public SAFileItem getNext () {

        for (SAFileItem item : queue) {
            if (!item.isOnDisk()) {
                return item;
            }
        }

        return null;

    }

    /**
     * Method that returns the current length of the queue
     *
     * @return shorthand for queue.size ()
     */
    public int getLength () {
        return queue.size();
    }
}
