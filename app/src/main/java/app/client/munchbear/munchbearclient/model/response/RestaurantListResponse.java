package app.client.munchbear.munchbearclient.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;

/**
 * @author Roman H.
 */

public class RestaurantListResponse implements Parcelable {

    @SerializedName("data")
    private List<Restaurant> restaurantList;

    public RestaurantListResponse() {

    }

    public RestaurantListResponse(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    protected RestaurantListResponse(Parcel in) {
        restaurantList = in.createTypedArrayList(Restaurant.CREATOR);
    }

    public static final Creator<RestaurantListResponse> CREATOR = new Creator<RestaurantListResponse>() {
        @Override
        public RestaurantListResponse createFromParcel(Parcel in) {
            return new RestaurantListResponse(in);
        }

        @Override
        public RestaurantListResponse[] newArray(int size) {
            return new RestaurantListResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(restaurantList);
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }
}
