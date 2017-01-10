/**
 * Copyright:   SuperAwesome Trading Limited 2017
 * Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.asynctask;

/**
 * This interface defines methods that will be implemented by SAAsyncTaks in order to represent
 * the three stages of an async operation: what custom task to execute, what happens on finish
 * and what happens on error.
 *
 * @param <T>   The interface runs over a generic T parameter than can be anything
 */
public interface SAAsyncTaskInterface <T> {

    /**
     * This method will define what should be executed. By default it should return a paramter
     * of type T, specified when creating the new interface implementation.
     * It can also throw an exception if needed.
     *
     * If the method returns an parameter T then it should mean that whatever async task was
     * executed finished successfully and the "onFinish" branch will then continue.
     *
     * If the method throws an exception then it means an error occurred while trying to
     * execute the async task and the "onError" branch will then continue.
     *
     * @return              a generic parameter of type T in case of success
     * @throws Exception    a generic exception in case of error
     */
    T taskToExecute () throws Exception;

    /**
     * This method will get called in case of async task success, with a generic parameter of type
     * T that may hold additional information to be passed back to the main execution thread of
     * the program.
     *
     * @param result    a generic param T
     */
    void onFinish (T result);

    /**
     * This method will get called in case of async task error. No error information will be
     * present, though.
     */
    void onError ();
}
