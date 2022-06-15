package app.client.munchbear.munchbearclient.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class OrderUserContact {

    @SerializedName("name")
    private String userName;

    @SerializedName("phone")
    private String userPhone;

    public OrderUserContact(String userName, String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
