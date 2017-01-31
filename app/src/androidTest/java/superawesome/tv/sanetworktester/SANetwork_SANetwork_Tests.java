package superawesome.tv.sanetworktester;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONObject;

import tv.superawesome.lib.sanetwork.request.SANetwork;
import tv.superawesome.lib.sanetwork.request.SANetworkInterface;

public class SANetwork_SANetwork_Tests extends ActivityInstrumentationTestCase2<MainActivity> {
    private static final int TIMEOUT = 2500;

    public SANetwork_SANetwork_Tests () {
        super("superawesome.tv.sanetworktester", MainActivity.class);
    }

    @UiThreadTest
    @LargeTest
    public void test1 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = "https://ads.staging.superawesome.tv/v2/ad/223";

        network.sendGET(getActivity(), url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertTrue(success);
                assertNotNull(payload);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test2 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = "https://ads.staging.superawesome.tv/v2/ad/223";

        network.sendGET(getActivity(), url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertTrue(success);
                assertNotNull(payload);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test3 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = null;

        network.sendGET(getActivity(), url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertFalse(success);
                assertNull(payload);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test4 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = "";

        network.sendGET(getActivity(), url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertFalse(success);
                assertNull(payload);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test5 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = "jkklsj///_txt.s.a";

        network.sendGET(getActivity(), url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertFalse(success);
                assertNull(payload);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test6 () {

        // the network object
        SANetwork network = new SANetwork();

        final String url = "https://ads.staging.superawesome.tv/v2/ad/223";

        network.sendGET(null, url, new JSONObject(), new JSONObject(), new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

                assertFalse(success);
                assertNull(payload);
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
