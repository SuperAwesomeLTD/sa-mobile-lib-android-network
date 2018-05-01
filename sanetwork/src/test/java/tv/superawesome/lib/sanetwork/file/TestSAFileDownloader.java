package tv.superawesome.lib.sanetwork.file;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.util.concurrent.Executor;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import okio.Buffer;
import tv.superawesome.lib.sanetwork.mocks.MockExecutor;
import tv.superawesome.lib.sanetwork.testutils.ResourceReader;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class TestSAFileDownloader {

    private Executor executor;
    private MockWebServer server;

    @Before
    public void setUp () throws Exception {
        executor = new MockExecutor();
        server = new MockWebServer();
        server.start();
    }

    @After
    public void tearDown () throws Exception {
        server.shutdown();
    }

    @Test
    public void test_SAFileDownloader_WithPNGFile () throws Exception {
        // given

        String url = server.url("/some/resource/url/pngresource.png").toString();
        Buffer responseBody = ResourceReader.readResource("pngresource.png");

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody);

        Context context = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        final FileOutputStream outputStream = new FileOutputStream("diskfile.png");

        // when
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
        when(prefs.edit()).thenReturn(editor);
        when(prefs.edit().putString(anyString(), anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);
        when(context.openFileOutput(anyString(), anyInt())).thenReturn(outputStream);

        server.enqueue(mockResponse);

        new SAFileDownloader(executor, true, 1000).downloadFileFrom(context, url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                Assert.assertTrue(success);
                Assert.assertNotNull(diskUrl);
                Assert.assertTrue(diskUrl.contains("samov_"));
                Assert.assertTrue(diskUrl.contains(".png"));
                Assert.assertNotNull(outputStream);

            }
        });

        // then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/resource/url/pngresource.png HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SAFileDownloader_WithMP4File () throws Exception {
        // given
        String url = server.url("/some/resource/url/videoresource.mp4").toString();
        Buffer responseBody = ResourceReader.readResource("videoresource.mp4");

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody);

        Context context = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        final FileOutputStream outputStream = new FileOutputStream("diskfile.png");

        // when
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
        when(prefs.edit()).thenReturn(editor);
        when(prefs.edit().putString(anyString(), anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);
        when(context.openFileOutput(anyString(), anyInt())).thenReturn(outputStream);

        server.enqueue(mockResponse);

        new SAFileDownloader(executor, true, 1000).downloadFileFrom(context, url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                Assert.assertTrue(success);
                Assert.assertNotNull(diskUrl);
                Assert.assertTrue(diskUrl.contains("samov_"));
                Assert.assertTrue(diskUrl.contains(".mp4"));
                Assert.assertNotNull(outputStream);
            }
        });

        // then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/resource/url/videoresource.mp4 HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SAFileDownloader_WithErrorOnAllRetries () throws Exception {
        // given
        String url = server.url("/some/resource/url/videoresource.mp4").toString();
        Buffer responseBody = ResourceReader.readResource("videoresource.mp4");

        MockResponse badResponse = new MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
                .setBody(responseBody);

        Context context = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        final FileOutputStream outputStream = new FileOutputStream("diskfile.png");

        // when
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
        when(prefs.edit()).thenReturn(editor);
        when(prefs.edit().putString(anyString(), anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);
        when(context.openFileOutput(anyString(), anyInt())).thenReturn(outputStream);

        server.enqueue(badResponse);

        new SAFileDownloader(executor, true, 1000).downloadFileFrom(context, url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                Assert.assertFalse(success);
                Assert.assertNull(diskUrl);
                Assert.assertNotNull(outputStream);
            }
        });
    }

    @Test
    public void test_SAFileDownloader_WithNullContext () throws Exception {
        // given
        String url = server.url("/some/resource/url/videoresource.mp4").toString();

        // when
        new SAFileDownloader(executor, true, 1000).downloadFileFrom(null, url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                Assert.assertFalse(success);
                Assert.assertNull(diskUrl);
            }
        });
    }

    @Test
    public void test_SAFileDownloader_WithMalformedUrl () throws Exception {
        // given
        Context context = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        final FileOutputStream outputStream = new FileOutputStream("diskfile.png");

        // when
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
        when(prefs.edit()).thenReturn(editor);
        when(prefs.edit().putString(anyString(), anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);
        when(context.openFileOutput(anyString(), anyInt())).thenReturn(outputStream);

        // when
        new SAFileDownloader(executor, true, 1000).downloadFileFrom(context, "jsaksa\\\\\\\\s\\\\\\\\asasaasa", new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String diskUrl) {

                Assert.assertFalse(success);
                Assert.assertNull(diskUrl);
            }
        });
    }
}
