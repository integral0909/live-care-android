package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.user.UserData;

/**
 * @author Roman H.
 */

public class GetUserDataResponse {

    @SerializedName("data")
    UserData userData;

    public GetUserDataResponse(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
