package tv.superawesome.lib.sanetwork.file;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class TestSAFileItem {

    @Test
    public void test_SAFileItem_WithNoUrl () {
        // given

        // when
        SAFileItem item = new SAFileItem();

        // then
        assertNotNull(item);
        assertNull(item.getUrlKey());
        assertNull(item.getDiskName());
        assertNull(item.getDiskUrl());
        assertNull(item.getKey());
        assertFalse(item.isOnDisk());
        assertEquals(0, item.getNrRetries());
        assertNotNull(item.getResponses());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithValidMP4Url () {
        // given
        String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertEquals(item.getUrlKey(), url);
        assertNotNull(item.getDiskName());
        assertNotNull(item.getDiskUrl());
        assertNotNull(item.getKey());
        assertTrue(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithValidPNGUrl () {
        // given
        String url = "https://s3-eu-west-1.amazonaws.com/sb-ads-uploads/images/VNyy2KSeGrvC0eO7DDOxMfWm2GQKuJ6X.png";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertEquals(item.getUrlKey(), url);
        assertNotNull(item.getDiskName());
        assertNotNull(item.getDiskUrl());
        assertNotNull(item.getKey());
        assertTrue(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithInvalidUrl () {
        // given
        String url = "jsjksalaslksalk";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNotNull(item.getDiskName());
        assertNotNull(item.getDiskUrl());
        assertNotNull(item.getKey());
        assertTrue(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithMalformedUrl () {
        // given
        String url = "90sa?/:SAjsako91lk/_21klj21.txt";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNotNull(item.getDiskName());
        assertNotNull(item.getDiskUrl());
        assertNotNull(item.getKey());
        assertTrue(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithNullUrl () {
        // given
        String url = null;

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNull(item.getDiskName());
        assertNull(item.getDiskUrl());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithEmptyUrl () {
        // given
        String url = "";

        // then
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNull(item.getDiskName());
        assertNull(item.getDiskUrl());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithUrlAndDiskKey () {
        // given
        String urlKey = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String diskUrl = "testfile.mp4";

        // when
        SAFileItem item = new SAFileItem();
        item.setUrlKey(urlKey);
        item.setDiskUrl(diskUrl);

        // then
        String expectedUrlKey = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String expectedDiskUrl = "testfile.mp4";

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
