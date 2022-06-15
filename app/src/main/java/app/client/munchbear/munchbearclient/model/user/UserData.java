package app.client.munchbear.munchbearclient.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Roman on 7/18/2018.
 */
public class UserData {

    public static final String KEY_USER_ID = "key.user.id";
    public static final String KEY_NAME = "key.name";
    public static final String KEY_PHONE = "key.phone";
    public static final String KEY_EMAIL = "key.email";

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("is_guest")
    private boolean isGuest;

    public UserData(int userId, String name, String phone, String email, boolean isGuest) {
        this.id = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isGuest = isGuest;
    }

    public UserData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public UserData() {

    }

    public int getUserId() {
        return id;
    }

    public void setUserId(int userId) {
        this.id = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }
}
