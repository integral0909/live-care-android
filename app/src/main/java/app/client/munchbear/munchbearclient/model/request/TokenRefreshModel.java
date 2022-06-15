package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.AppConfig;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

/*
 * @author Roman H.
 */
public class TokenRefreshModel {

    @SerializedName("grant_type")
    private String grantType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("scope")
    private String scope;

    public TokenRefreshModel(String scope) {
        this.refreshToken = CorePreferencesImpl.getCorePreferences().getRefreshToken();
        this.grantType = "refresh_token";
        this.clientSecret = AppConfig.CLIENT_SECRET;
        this.clientId = AppConfig.CLIENT_ID;
        this.scope = scope;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
