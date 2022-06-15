package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.menu.RestaurantMenu;

/**
 * @author Roman H.
 */

public class RestaurantMenuResponse {

    @SerializedName("data")
    private RestaurantMenu restaurantMenu;

    public RestaurantMenuResponse(RestaurantMenu restaurantMenu) {
        this.restaurantMenu = restaurantMenu;
    }

    public RestaurantMenu getRestaurantMenu() {
        return restaurantMenu;
    }

    public void setRestaurantMenu(RestaurantMenu restaurantMenu) {
        this.restaurantMenu = restaurantMenu;
    }
}
