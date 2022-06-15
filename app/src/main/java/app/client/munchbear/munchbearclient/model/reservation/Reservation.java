package app.client.munchbear.munchbearclient.model.reservation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class Reservation implements Parcelable {

    public static final String KEY_IS_RESERVATION = "key.is.reservation";
    public static final String ACTION_RESERVATION_UPDATED = "action.reservation.updated";

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("status")
    private int status;

    @SerializedName("child_chairs")
    private int childChairs;

    @SerializedName("amount_of_people")
    private int amountOfPeople;

    @SerializedName("comment")
    private String comment;

    @SerializedName("when")
    private long when;

    public Reservation(int id, String name, String phone, int status, int childChairs, int amountOfPeople, String comment, long when) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.childChairs = childChairs;
        this.amountOfPeople = amountOfPeople;
        this.comment = comment;
        this.when = when;
    }

    protected Reservation(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phone = in.readString();
        status = in.readInt();
        childChairs = in.readInt();
        amountOfPeople = in.readInt();
        comment = in.readString();
        when = in.readLong();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeInt(status);
        dest.writeInt(childChairs);
        dest.writeInt(amountOfPeople);
        dest.writeString(comment);
        dest.writeLong(when);
    }
}

