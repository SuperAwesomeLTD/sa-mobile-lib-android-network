package superawesome.tv.sanetworktester;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;

import tv.superawesome.lib.sanetwork.file.SAFileDownloader;
import tv.superawesome.lib.sanetwork.file.SAFileDownloaderInterface;

public class SANetwork_SAFileDownloader_Tests extends ActivityInstrumentationTestCase2 <MainActivity> {

    private static final int TIMEOUT = 2500;

    public SANetwork_SAFileDownloader_Tests() {
        super("superawesome.tv.sanetworktester", MainActivity.class);
    }

    @UiThreadTest
    @LargeTest
    public void test1 () {

        // the network object
        final String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertTrue(success);
                assertNotNull(diskUrl);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test2 () {

        final String url = "https://s3-eu-west-1.amazonaws.com/sb-ads-uploads/images/VNyy2KSeGrvC0eO7DDOxMfWm2GQKuJ6X.png";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertTrue(success);
                assertNotNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test3 () {

        final String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/SGxtByw2Mc6se9jSCus6Hn5IuxNGh9f4.mp4";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertTrue(success);
                assertNotNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test4 () {

        final String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/SGxtByw2Mc6se9jSCus6Hn5IuxNGh9f4.mp4";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertTrue(success);
                assertNotNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test5 () {

        final String url = null;

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertFalse(success);
                assertNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test6 () {

        final String url = "90sa?/:SAjsako91lk/_21klj21.txt";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertFalse(success);
                assertNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test7 () {

        final String url = "";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertFalse(success);
                assertNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test8 () {

        final String url = "jkjskajksjak";

        SAFileDownloader.getInstance().downloadFileFrom(getActivity(), url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertFalse(success);
                assertNull(diskUrl);

            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test9 () {

        final String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";

        SAFileDownloader.getInstance().downloadFileFrom(null, url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                assertFalse(success);
                assertNull(diskUrl);

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
