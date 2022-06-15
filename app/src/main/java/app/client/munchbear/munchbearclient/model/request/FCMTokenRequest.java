package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class FCMTokenRequest {

    @SerializedName("token")
    private String fcmToken;

    public FCMTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
