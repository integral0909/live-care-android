package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.AppConfig;

/**
 * @author Roman H.
 */

public class LoginBaseRequestModel {

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("client_id")
    private String clientId;

    public LoginBaseRequestModel() {
        this.clientSecret = AppConfig.CLIENT_SECRET;
        this.clientId = AppConfig.CLIENT_ID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
