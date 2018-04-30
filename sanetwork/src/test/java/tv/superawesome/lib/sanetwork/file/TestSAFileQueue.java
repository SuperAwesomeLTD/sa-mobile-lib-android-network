package tv.superawesome.lib.sanetwork.file;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class TestSAFileQueue {

    @Test
    public void test_SAFileQueue_WithNoNextItem () {
        // given
        SAFileItem expectedNext = null;

        // when
        SAFileQueue queue = new SAFileQueue();
        int expectedLength = 0;

        // then
        int length = queue.getLength();
        SAFileItem next = queue.getNext();

        assertNotNull(queue);
        assertEquals(length, expectedLength);
        assertEquals(next, expectedNext);
    }

    @Test
    public void test_SAFileQueue_WithMultipleItems () {
        // given
        String url1 = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String url2 = "https://s3-eu-west-1.amazonaws.com/sb-ads-uploads/images/VNyy2KSeGrvC0eO7DDOxMfWm2GQKuJ6X.png";

        SAFileItem item1 = new SAFileItem(url1);
        SAFileItem item2 = new SAFileItem(url2);


        // when
        SAFileQueue queue = new SAFileQueue();
        queue.addToQueue(item1);
        queue.addToQueue(item2);

        // then
        assertNotNull(queue);
        assertEquals(queue.getLength(), 2);

        // pop queue
        assertNotNull(queue.getNext());

        queue.removeFromQueue(item1);
        assertEquals(queue.getLength(), 1);

        assertFalse(queue.hasItemForURL(url1));
        assertTrue(queue.hasItemForURL(url2));

        assertNull(queue.itemForURL(url1));
        assertNotNull(queue.itemForURL(url2));

        queue.addToQueue(null);
        assertEquals(queue.getLength(), 1);

        queue.removeFromQueue(null);
        assertEquals(queue.getLength(), 1);
    }

    @Test
    public void test_SAFileQueue_WithNullFileItem () {
        // given
        SAFileItem item = null;

        // when
        SAFileQueue queue = new SAFileQueue();
        queue.addToQueue(item);

        // then
        assertNotNull(queue);
        assertEquals(queue.getLength(), 0);
        assertNull(queue.getNext());
    }
}
