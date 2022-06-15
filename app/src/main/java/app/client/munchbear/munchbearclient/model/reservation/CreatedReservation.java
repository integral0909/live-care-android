package app.client.munchbear.munchbearclient.model.reservation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;

/**
 * @author Roman H.
 */

public class CreatedReservation extends Reservation implements Parcelable {

    public static final String KEY_CREATED_RESERVATION = "created.reservation.key";

    @SerializedName("restaurant")
    private Restaurant restaurant;

    public CreatedReservation(int id, String name, String phone, int status, int childChairs, int amountOfPeople,
                              String comment, long when, Restaurant restaurant) {
        super(id, name, phone, status, childChairs, amountOfPeople, comment, when);
        this.restaurant = restaurant;
    }

    protected CreatedReservation(Parcel in) {
        super(in);
        restaurant = in.readParcelable(Restaurant.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(restaurant, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreatedReservation> CREATOR = new Creator<CreatedReservation>() {
        @Override
        public CreatedReservation createFromParcel(Parcel in) {
            return new CreatedReservation(in);
        }

        @Override
        public CreatedReservation[] newArray(int size) {
            return new CreatedReservation[size];
        }
    };

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
