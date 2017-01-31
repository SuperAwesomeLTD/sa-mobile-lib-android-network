package superawesome.tv.sanetworktester;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.Arrays;
import java.util.List;

import tv.superawesome.lib.sanetwork.listdownload.SAFileListDownloader;
import tv.superawesome.lib.sanetwork.listdownload.SAFileListDownloaderInterface;

public class SANetwork_SAFileListDownloader_Tests extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final int TIMEOUT = 5000;

    public SANetwork_SAFileListDownloader_Tests() {
        super("superawesome.tv.sanetworktester", MainActivity.class);
    }

    @UiThreadTest
    @LargeTest
    public void test1 () {

        // try to download images
        List<String> urls = Arrays.asList(
                "https://ads.superawesome.tv/v2/demo_images/320x50.jpg",
                "https://ads.superawesome.tv/v2/demo_images/300x250.jpg",
                "https://ads.superawesome.tv/v2/demo_images/320x480.jpg",
                "https://ads.superawesome.tv/v2/demo_images/480x320.jpg",
                "https://ads.superawesome.tv/v2/demo_images/728x90.jpg",
                "https://ads.superawesome.tv/v2/demo_images/300x50.jpg");
        SAFileListDownloader listDownloader = new SAFileListDownloader(getActivity());
        listDownloader.downloadListOfFiles(urls, new SAFileListDownloaderInterface() {
            @Override
            public void saDidDownloadFilesInList(List<String> diskLocations) {

                // see that all files have been downloaded
                assertEquals(diskLocations.size(), 6);

                // assert each of the files is not null
                for (String file : diskLocations) {
                    assertNotNull(file);
                    assertTrue(file.contains("samov_"));
                }
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test2 () {

        // try to download images
        List<String> urls1 = Arrays.asList(
                "90sa?/:SAjsako91lk/_21klj211.txt",
                "jkjskajksjak222");
        SAFileListDownloader listDownloader1 = new SAFileListDownloader(getActivity());
        listDownloader1.downloadListOfFiles(urls1, new SAFileListDownloaderInterface() {
            @Override
            public void saDidDownloadFilesInList(List<String> diskLocations) {

                // assert that it still filed the diskLocations array with data
                assertEquals(diskLocations.size(), 2);

                // assert files are null
                for (String file : diskLocations) {
                    assertNull(file);
                }
            }
        });

        sleep(TIMEOUT);
    }

    private void sleep(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            fail("Unexpected Timeout");
        }
    }
}
