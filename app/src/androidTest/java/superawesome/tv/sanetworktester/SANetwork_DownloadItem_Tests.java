package superawesome.tv.sanetworktester;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
import java.util.List;

import tv.superawesome.lib.sanetwork.file.SAFileItem;
import tv.superawesome.lib.sanetwork.file.SAFileDownloaderInterface;

/**
 * Created by gabriel.coman on 20/10/16.
 */
public class SANetwork_DownloadItem_Tests extends ApplicationTestCase<Application> {
    public SANetwork_DownloadItem_Tests() {
        super(Application.class);
    }

    @SmallTest
    public void testSimple () {
        // given
        SAFileItem item = new SAFileItem();

        // when
        String expectedUrlKey = null;
        String expectedDiskName = null;
        String expectedDiskUrl = null;
        String expectedKey = null;
        boolean expectedIsOnDisk = false;
        int expectedNrRetries = 0;
        List <SAFileDownloaderInterface> expectedResponses = new ArrayList<>();

        // then
        String urlKey = item.getUrlKey();
        String diskName = item.getDiskName();
        String diskUrl = item.getDiskUrl();
        String key = item.getKey();
        boolean isOnDisk = item.isOnDisk();
        int nrRetries = item.getNrRetries();
        List<SAFileDownloaderInterface> responses = item.getResponses();

        // assert
        assertNotNull(item);
        assertEquals(urlKey, expectedUrlKey);
        assertEquals(diskName, expectedDiskName);
        assertEquals(diskUrl, expectedDiskUrl);
        assertEquals(key, expectedKey);
        assertEquals(isOnDisk, expectedIsOnDisk);
        assertEquals(nrRetries, expectedNrRetries);
        assertEquals(responses, expectedResponses);
        assertFalse(item.isValid());
    }

    @SmallTest
    public void testWithUrls () {
        // given
        String url1 = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String url2 = "https://s3-eu-west-1.amazonaws.com/sb-ads-uploads/images/VNyy2KSeGrvC0eO7DDOxMfWm2GQKuJ6X.png";
        SAFileItem item1 = new SAFileItem(url1);
        SAFileItem item2 = new SAFileItem(url2);

        // when

        // then
        assertNotNull(item1);
        assertNotNull(item2);
        assertEquals(item1.getUrlKey(), url1);
        assertEquals(item2.getUrlKey(), url2);
        assertNotNull(item1.getDiskName());
        assertNotNull(item2.getDiskName());
        assertNotNull(item1.getDiskUrl());
        assertNotNull(item2.getDiskUrl());
        assertNotNull(item1.getKey());
        assertNotNull(item2.getKey());
        assertTrue(item1.isValid());
        assertTrue(item2.isValid());
    }

    @SmallTest
    public void testWithErrors () {
        // given
        String url1 = "jsjksalaslksalk";
        String url2 = "90sa?/:SAjsako91lk/_21klj21.txt";
        String url3 = null;
        String url4 = "";

        SAFileItem item1 = new SAFileItem(url1);
        SAFileItem item2 = new SAFileItem(url2);
        SAFileItem item3 = new SAFileItem(url3);
        SAFileItem item4 = new SAFileItem(url4);

        // then
        assertNotNull(item1);
        assertNotNull(item2);
        assertNotNull(item3);
        assertNotNull(item4);

        assertNotNull(item1.getDiskName());
        assertNotNull(item2.getDiskName());
        assertNull(item3.getDiskName());
        assertNull(item4.getDiskName());

        assertNotNull(item1.getDiskUrl());
        assertNotNull(item2.getDiskUrl());
        assertNull(item3.getDiskUrl());
        assertNull(item4.getDiskUrl());

        assertNotNull(item1.getKey());
        assertNotNull(item2.getKey());
        assertNull(item3.getKey());
        assertNull(item4.getKey());

        assertTrue(item1.isValid());
        assertTrue(item2.isValid());
        assertFalse(item3.isValid());
        assertFalse(item4.isValid());
    }

    @SmallTest
    public void testComplex () {
        // given
        String urlKey = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String diskUrl = "testfile.mp4";
        SAFileItem item = new SAFileItem();
        item.setUrlKey(urlKey);
        item.setDiskUrl(diskUrl);

        // when
        String expectedUrlKey = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String expectedDiskUrl = "testfile.mp4";

        // then
        assertNotNull(item);
        assertEquals(item.getUrlKey(), expectedUrlKey);
        assertEquals(item.getDiskUrl(), expectedDiskUrl);

        assertEquals(item.getNrRetries(), 0);
        assertTrue(item.hasRetriesRemaining());

        item.incrementNrRetries();
        assertEquals(item.getNrRetries(), 1);
        assertTrue(item.hasRetriesRemaining());

        item.incrementNrRetries();
        assertEquals(item.getNrRetries(), 2);
        assertTrue(item.hasRetriesRemaining());

        item.incrementNrRetries();
        assertEquals(item.getNrRetries(), 3);
        assertFalse(item.hasRetriesRemaining());

        SAFileDownloaderInterface r1 = new SAFileDownloaderInterface() { @Override public void saDidDownloadFile(boolean success, String diskUrl) {}};
        SAFileDownloaderInterface r2 = null;

        item.addResponse(r1);
        item.addResponse(r2);

        assertEquals(item.getResponses().size(), 1);

        item.clearResponses();

        assertEquals(item.getResponses().size(), 0);
    }
}
