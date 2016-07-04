package tv.superawesome.lib.sanetwork.request;

/**
 * This is a listener interface for SAGet and SAPost async task classes
 */
public interface SANetworkInterface {

    /**
     * This function should be called in case of Async operation success, and should
     * always return an anonymous data object
     *
     * @param data - is a callback parameter; to be accessed by the class that implements
     * this Listener interface
     */
    void success(SANetworkResponse response);

    /**
     * This function should be called in case of Async operation failure, and
     * should have no parameters
     */
    void failure();

}
