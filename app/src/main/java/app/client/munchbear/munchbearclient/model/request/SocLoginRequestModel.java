package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.AppConfig;

/**
 * @author Roman H.
 */

public class SocLoginRequestModel {

    @SerializedName("token")
    private String token;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("scope")
    private String scope;

    public SocLoginRequestModel(String token, String scope) {
        this.token = token;
        this.clientSecret = AppConfig.CLIENT_SECRET;
        this.clientId = AppConfig.CLIENT_ID;
        this.scope = scope;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
