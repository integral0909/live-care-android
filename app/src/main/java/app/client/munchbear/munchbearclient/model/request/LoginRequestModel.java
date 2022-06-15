package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.AppConfig;

/**
 * @author Roman H.
 */

public class LoginRequestModel extends LoginBaseRequestModel{

    @SerializedName("email")
    private String username;

    @SerializedName("password")
    private String password;

    public LoginRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
