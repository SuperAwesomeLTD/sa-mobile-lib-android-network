package tv.superawesome.lib.sanetwork.request;

/**
 * This is a listener interface for SAGet and SAPost async task classes
 */
public interface SARequestInterface {

    /**
     * This function should be called in case of Async operation response, and should
     * always return an anonymous data object
     *
     * @param status - the status of the call
     * @param payload - is a callback parameter; to be accessed by the class that implements
     * @param success - a boolean that tells whether the request is a success or not
     * this Listener interface
     */
    void response(int status, String payload, boolean success);
}
