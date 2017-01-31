package superawesome.tv.sanetworktester;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import tv.superawesome.lib.sanetwork.file.SAFileItem;
import tv.superawesome.lib.sanetwork.file.SAFileQueue;

/**
 * Created by gabriel.coman on 20/10/16.
 */
public class SANetwork_DownloadQueue_Tests extends ApplicationTestCase<Application> {
    public SANetwork_DownloadQueue_Tests() {
        super(Application.class);
    }

    @SmallTest
    public void testDefault () {
        // given
        SAFileQueue queue = new SAFileQueue();

        // when
        int expectedLength = 0;
        SAFileItem expectedNext = null;

        // then
        int length = queue.getLength();
        SAFileItem next = queue.getNext();

        // assert
        assertNotNull(queue);
        assertEquals(length, expectedLength);
        assertEquals(next, expectedNext);
    }

    @SmallTest
    public void testComplex () {
        // given
        SAFileQueue queue = new SAFileQueue();

        // then
        String url1 = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/5E827ejOz2QYaRWqyJpn15r1NyvInPy9.mp4";
        String url2 = "https://s3-eu-west-1.amazonaws.com/sb-ads-uploads/images/VNyy2KSeGrvC0eO7DDOxMfWm2GQKuJ6X.png";

        SAFileItem item1 = new SAFileItem(url1);
        SAFileItem item2 = new SAFileItem(url2);

        queue.addToQueue(item1);
        queue.addToQueue(item2);

        // assert
        assertNotNull(queue);
        assertEquals(queue.getLength(), 2);

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

    @SmallTest
    public void testErrors () {
        // given
        SAFileQueue queue = new SAFileQueue();

        // then
        SAFileItem item = null;
        queue.addToQueue(item);

        assertNotNull(queue);
        assertEquals(queue.getLength(), 0);
        assertNull(queue.getNext());
    }
}
