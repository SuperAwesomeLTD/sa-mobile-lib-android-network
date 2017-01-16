/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.request;

/**
 * Interface that is used by SANetwork to send a callback when an async network operation
 * finishes.
 */
public interface SANetworkInterface {

    /**
     * Interface method that gets called as a callback saDidGetResponse to an async network operation
     *
     * @param status    the HTTP status of the network call
     * @param payload   the payload of the network operation, as a String parameter
     * @param success   whether the operation was successful or not
     */
    void saDidGetResponse(int status, String payload, boolean success);
}
