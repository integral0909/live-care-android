package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class CreateReservationRequestModel {

    @SerializedName("when")
    private String when;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("child_chairs")
    private int childChairs;

    @SerializedName("amount_of_people")
    private int amountOfPeople;

    @SerializedName("comment")
    private String comment;

    public CreateReservationRequestModel() {

    }

    public CreateReservationRequestModel(String when, String name, String phone, int childChairs, int amountOfPeople, String comment) {
        this.when = when;
        this.name = name;
        this.phone = phone;
        this.childChairs = childChairs;
        this.amountOfPeople = amountOfPeople;
        this.comment = comment;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
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

    public int getChildChairs() {
        return childChairs;
    }

    public void setChildChairs(int childChairs) {
        this.childChairs = childChairs;
    }

    public int getAmountOfPeople() {
        return amountOfPeople;
    }

    public void setAmountOfPeople(int amountOfPeople) {
        this.amountOfPeople = amountOfPeople;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}