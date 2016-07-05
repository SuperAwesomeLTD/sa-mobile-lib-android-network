package tv.superawesome.lib.sanetwork.request;

/**
 * This is a listener interface for SAGet and SAPost async task classes
 */
public interface SANetworkInterface {

    /**
     * This function should be called in case of Async operation success, and should
     * always return an anonymous data object
     *
     * @param status - the status of the call
     * @param response - is a callback parameter; to be accessed by the class that implements
     * this Listener interface
     */
    void success(int status, String response);

    /**
     * This function should be called in case of Async operation failure, and
     * should have no parameters
     */
    void failure();

}
