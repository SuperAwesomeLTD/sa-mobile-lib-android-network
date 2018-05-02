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
        assertNull(item.getUrl());
        assertNull(item.getFileName());
        assertNull(item.getFilePath());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithValidUrl () {
        // given
        String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);

        assertNotNull(item.getUrl());

        assertNotNull(item.getFileName());
        assertEquals("5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4", item.getFileName());

        assertNotNull(item.getFilePath());
        assertEquals("5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4", item.getFilePath());

        assertNotNull(item.getKey());
        assertEquals("sasdkkey__5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4", item.getKey());

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
        assertNull(item.getUrl());
        assertNull(item.getFileName());
        assertNull(item.getFilePath());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithMalformedUrl () {
        // given
        String url = "90sa?/:SAjsako91lk/_21klj21.txt";

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNull(item.getUrl());
        assertNull(item.getFileName());
        assertNull(item.getFilePath());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }

    @Test
    public void test_SAFileItem_WithNullUrl () {
        // given
        String url = null;

        // when
        SAFileItem item = new SAFileItem(url);

        // then
        assertNotNull(item);
        assertNull(item.getUrl());
        assertNull(item.getFileName());
        assertNull(item.getFilePath());
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
        assertNull(item.getUrl());
        assertNull(item.getFileName());
        assertNull(item.getFilePath());
        assertNull(item.getKey());
        assertFalse(item.isValid());
    }
}
