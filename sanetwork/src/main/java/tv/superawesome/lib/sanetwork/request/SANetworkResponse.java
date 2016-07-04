package tv.superawesome.lib.sanetwork.request;

/**
 * Created by gabriel.coman on 04/07/16.
 */
public class SANetworkResponse {
    public int status;
    public String payload;

    public SANetworkResponse(int status, String payload) {
        this.status = status;
        this.payload = payload;
    }
}
