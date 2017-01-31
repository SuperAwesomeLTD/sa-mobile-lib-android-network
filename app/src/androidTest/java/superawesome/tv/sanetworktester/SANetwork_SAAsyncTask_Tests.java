package superawesome.tv.sanetworktester;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;

import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTask;
import tv.superawesome.lib.sanetwork.asynctask.SAAsyncTaskInterface;

public class SANetwork_SAAsyncTask_Tests extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final int TIMEOUT = 2500;

    public SANetwork_SAAsyncTask_Tests() {
        super("superawesome.tv.sanetworktester", MainActivity.class);
    }

    @UiThreadTest
    @LargeTest
    public void test1 () {

        new SAAsyncTask<>(getActivity(), new SAAsyncTaskInterface<Boolean>() {
            @Override
            public Boolean taskToExecute() throws Exception {
                return true;
            }

            @Override
            public void onFinish(Boolean result) {
                assertTrue(result);
            }

            @Override
            public void onError() {
                fail();
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test2 () {

        new SAAsyncTask<>(null, new SAAsyncTaskInterface<Boolean>() {
            @Override
            public Boolean taskToExecute() throws Exception {
                throw new Exception();
            }

            @Override
            public void onFinish(Boolean result) {
                assertTrue(result);
            }

            @Override
            public void onError() {
                assertTrue(true);
            }
        });

        sleep(TIMEOUT);
    }

    @UiThreadTest
    @LargeTest
    public void test3 () {

        new SAAsyncTask<>(null, new SAAsyncTaskInterface<Boolean>() {
            @Override
            public Boolean taskToExecute() throws Exception {
                return true;
            }

            @Override
            public void onFinish(Boolean result) {
                assertTrue(result);
            }

            @Override
            public void onError() {
                assertTrue(true);
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
