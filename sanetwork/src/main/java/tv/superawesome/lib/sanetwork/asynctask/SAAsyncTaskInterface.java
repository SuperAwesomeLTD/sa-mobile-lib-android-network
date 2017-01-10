package tv.superawesome.lib.sanetwork.asynctask;

/**
 * Created by gabriel.coman on 25/05/16.
 */
public interface SAAsyncTaskInterface <T> {
    T taskToExecute() throws Exception;
    void onFinish(T result);
    void onError();
}
