package tv.superawesome.lib.sanetwork.request;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import tv.superawesome.lib.sanetwork.mocks.MockExecutor;
import tv.superawesome.lib.sanetwork.request.SANetwork;
import tv.superawesome.lib.sanetwork.request.SANetworkInterface;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class TestSANetwork {

    private final String responseBody = "{\"name\":\"John\", \"age\":23}";
    private SANetwork network;
    private MockWebServer server;

    @Before
    public void setUp () throws Exception {
        server = new MockWebServer();
        server.start();

        Executor executor = new MockExecutor();
        network = new SANetwork(executor, 1000);
    }

    @After
    public void tearDown () throws Exception {
        server.shutdown();
    }

    @Test
    public void test_SANetwork_SendGET_WithSuccess () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse().setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(200, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals(responseBody, payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_WithQueryAndSuccess () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        JSONObject query = new JSONObject();
        query.put("userId", "50");
        query.put("limit", 10);
        MockResponse mockResponse = new MockResponse().setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, query, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(200, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals(responseBody, payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url?limit=10&userId=50 HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_WithHeadersAndSuccess () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("X-Version", "ios-1.0.0");
        MockResponse mockResponse = new MockResponse().setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, header, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(200, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals(responseBody, payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
        assertEquals("application/json", record.getHeader("Content-Type"));
        assertEquals("ios-1.0.0", record.getHeader("X-Version"));
    }

    @Test
    public void test_SANetwork_SendPOST_WithSuccess () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("X-Version", "ios-1.0.0");
        MockResponse mockResponse = new MockResponse().setBody(responseBody);

        JSONObject body = new JSONObject();
        body.put("value", 23);
        body.put("username", "johhny@");

        // when
        server.enqueue(mockResponse);

        network.sendPOST(url, null, header, body, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(200, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals(responseBody, payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("POST /some/url HTTP/1.1", record.getRequestLine());
        assertEquals("application/json", record.getHeader("Content-Type"));
        assertEquals("ios-1.0.0", record.getHeader("X-Version"));
        assertEquals(body.toString(), record.getBody().readUtf8());
    }

    @Test
    public void test_SANetwork_SendGET_WithEmptyResponse () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse().setResponseCode(204);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(204, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals("", payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_WithSlowResponse () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        int delay = 500;
        MockResponse mockResponse = new MockResponse()
                .setBodyDelay(delay, TimeUnit.MILLISECONDS)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(200, status);
                assertTrue(success);
                assertNotNull(payload);
                assertEquals(responseBody, payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_With404Resposne () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(404)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(404, status);
                assertFalse(success);
                assertNull(payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_With401Response () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(401)
                .removeHeader("WWW-Authenticate")
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(401, status);
                assertFalse(success);
                assertNull(payload);
            }
        });

        //then
        RecordedRequest record = server.takeRequest();
        assertEquals("GET /some/url HTTP/1.1", record.getRequestLine());
    }

    @Test
    public void test_SANetwork_SendGET_WithMalformedUrl () throws Exception {
        // given
        network.sendGET("jsaksa\\\\s\\\\asasaasa", null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(0, status);
                assertFalse(success);
                assertNull(payload);
            }
        });
    }

    @Test
    public void test_SANetwork_SendGET_WithResponseTimeout () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(0, status);
                assertFalse(success);
                assertNull(payload);
            }
        });
    }

    @Test
    public void test_SANetwork_SendGET_WithDisconnectAfterRequest () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(0, status);
                assertFalse(success);
                assertNull(payload);
            }
        });
    }

    @Test
    public void test_SANetwork_SendGET_WithRequestBodyDisconnected () throws Exception {
        // given
        String url = server.url("/some/url").toString();

        JSONObject body = new JSONObject();
        body.put("value", 23);
        body.put("username", "johhny@");

        MockResponse mockResponse = new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_REQUEST_BODY)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendPOST(url, null, null, body, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(0, status);
                assertFalse(success);
                assertNull(payload);
            }
        });
    }

    @Test
    public void test_SANetwork_SendGET_WithDisconnectAtStart () throws Exception {
        // given
        String url = server.url("/some/url").toString();
        MockResponse mockResponse = new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
                .setBody(responseBody);

        // when
        server.enqueue(mockResponse);

        network.sendGET(url, null, null, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                // then
                assertEquals(0, status);
                assertFalse(success);
                assertNull(payload);
            }
        });
    }
}
