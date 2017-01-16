/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.listdownload;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tv.superawesome.lib.sanetwork.file.SAFileDownloader;
import tv.superawesome.lib.sanetwork.file.SAFileDownloaderInterface;

/**
 * Class that abstracts all the problems of downloading several files one-at-a-time in a list
 */
public class SAFileListDownloader {

    // copy of the context
    private Context context = null;

    // copy of a SAFileListDownloaderInterface implementation to be able to return a callback
    // to library users
    private SAFileListDownloaderInterface listener = null;

    /**
     * Normal constructor that takes a context as a param and initializes the local
     * listener so that it can be used without null checks
     *
     * @param context current context (activity or fragment)
     */
    public SAFileListDownloader(Context context) {
        this.context = context;
        this.listener = new SAFileListDownloaderInterface() {@Override public void saDidDownloadFilesInList(List<String> diskLocations) {}};
    }

    /**
     * Main method that starts the download process
     *
     * @param files     a list of remote files
     * @param listener  the final listener to give library users a way of knowing when it will
     *                  all end
     */
    public void downloadListOfFiles(final List<String> files, final SAFileListDownloaderInterface listener) {
        // copy a reference to the listener
        this.listener = listener != null ? listener : this.listener;

        // some additional helper vars
        final int max = files.size();
        final int[] totalFilesDownloaded = {0};

        // an array of  files that have actually been downloaded
        final List<SAFileListItem> filesDownloaded = new ArrayList<>();

        // go through all the list of files to download and start the download process
        for (int i = 0; i < max; i++) {

            final int finalI = i;
            getFile(context, i, files.get(i), new SAFileListItemInterface() {
                /**
                 * Override method from the SAFileListItemInterface that acts as callback method
                 * containing the index (order) the file has been downloaded in and the
                 * new disk url
                 *
                 * @param index     the order the file has been downloaded by SAFileDownloader
                 * @param success   Whether the network operation to get the file was a success
                 * @param diskUrl   the new disk url
                 */
                @Override
                public void didDownloadFileAtIndex(int index, boolean success, String diskUrl) {

                    Log.d("SuperAwesome", "List file at original index: " + finalI + " got put on " + index + " with Disk Url:  " + diskUrl);

                    // add to the array of files that have been downloaded
                    filesDownloaded.add(new SAFileListItem(index, diskUrl));

                    // increment the download counter
                    totalFilesDownloaded[0]++;

                    // if there are no more files to download
                    if (totalFilesDownloaded[0] == max) {

                        // sort the array ascending
                        Arrays.sort(filesDownloaded.toArray());

                        // transform the array of SAFileListItems to just a string one
                        List<String> finalFiles = new ArrayList<>();
                        for (SAFileListItem fileItem : filesDownloaded) {
                            finalFiles.add(fileItem.getFile());
                        }

                        // call to the final listener
                        SAFileListDownloader.this.listener.saDidDownloadFilesInList(finalFiles);
                    }

                }
            });

        }
    }

    /**
     * Method that wraps the SAFileDownloader "downloadFileFrom" method and returns a callback
     * listener that specifies the order in which the file should be put back as well as
     * the disk url
     *
     * @param context   current context (activity or fragment)
     * @param i         the order (array index) at which the new result should be put in
     * @param file      the remote file url to be downloaded
     * @param listener  instance of the SAFileListItemInterface to send the message back to the
     *                  calling method
     */
    private void getFile (final Context context, final int i, String file, final SAFileListItemInterface listener) {

        SAFileDownloader.getInstance().downloadFileFrom(context, file, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                // call on this
                listener.didDownloadFileAtIndex(i, success, diskUrl);

            }
        });

    }
}

/**
 * Private class that defines a File List item - basically a store for a disk url and
 * the order in which it should be place back on the list
 */
class SAFileListItem implements Comparable<SAFileListItem> {

    // private vars
    private int index;
    private String file;

    /**
     * Constructor with index and file as param
     *
     * @param index integer index to know how to assemble it back
     * @param file  disk file url
     */
    SAFileListItem(int index, String file) {
        this.index = index;
        this.file = file;
    }

    /**
     * Getter for the file url
     *
     * @return a string
     */
    String getFile() {
        return file;
    }

    /**
     * Overridden comparable protocol method describing how the list item should be sorted
     *
     * @param another   SAFileListItem instance
     * @return          either 1, -1 or 0 (for "greater", "lower" and "equal"
     */
    @Override
    public int compareTo(SAFileListItem another) {
        if (index > another.index) return 1;
        if (index < another.index) return -1;
        return 0;
    }
}

/**
 * Private interface needed by the private "getFile" method in "SAFileListDownloader" so that
 * when I send back data to "downloadListOfFiles" I know how to rearrange the resulting
 * disk urls in a list, considering the order in which they've been downloaded might not be
 * the same as the one specified
 *
 */
interface SAFileListItemInterface {

    /**
     * Method to implement in the interface
     *
     * @param index     an index to send back
     * @param success   Whether the network operation to get the file was a success
     * @param diskUrl   a disk url after the file has been downloaded
     */
    void didDownloadFileAtIndex(int index, boolean success, String diskUrl);
}