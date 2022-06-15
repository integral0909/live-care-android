package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class UpdateReservationRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    public UpdateReservationRequest(String name, String phone) {
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
