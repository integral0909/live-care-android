package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.user.UserData;

/**
 * @author Roman H.
 */

public class LoginResponse {

    @SerializedName("data")
    private UserData user;

    @SerializedName("token")
    private String token;

    public LoginResponse(UserData user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
