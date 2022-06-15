package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class RegisterRequestModel extends LoginRequestModel {

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    public RegisterRequestModel(String name, String email, String phone, String password) {
        super(email, password);
        this.name = name;
        this.phone = phone;
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
}
