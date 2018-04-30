package tv.superawesome.lib.sanetwork.mocks;

import java.util.concurrent.Executor;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class MockExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}