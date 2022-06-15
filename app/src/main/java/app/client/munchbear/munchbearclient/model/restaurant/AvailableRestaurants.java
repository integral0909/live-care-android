package app.client.munchbear.munchbearclient.model.restaurant;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;

/**
 * @author Roman H.
 *
 * Singleton class where saving all available restaurants.
 * Getting data for this class occurs after starter application.
 */

public class AvailableRestaurants {

    private static AvailableRestaurants availableRestaurantsInstance;

    private List<Restaurant> deliveryRestaurantList;
    private List<Restaurant> baseRestaurantList;
    private List<Restaurant> reservationRestaurantList;

    private boolean needRefresh = true;

    private AvailableRestaurants() {

    }

    public static AvailableRestaurants getInstance() {
        if (availableRestaurantsInstance == null) {
            availableRestaurantsInstance = new AvailableRestaurants();
        }

        return availableRestaurantsInstance;
    }

    public boolean isRestaurantListEmpty(Context context, boolean isDelivery) {
        return isDelivery ? checkRestaurantListSize(context, deliveryRestaurantList) :
                checkRestaurantListSize(context, baseRestaurantList);
    }

    private boolean checkRestaurantListSize(Context context, List<Restaurant> restaurantList) {
        if (restaurantList != null && restaurantList.size() > 0) {
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.toast_restaurant_list_empty), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public int getFirstRestaurantId() {
        if (baseRestaurantList != null && baseRestaurantList.size() > 0) {
            return baseRestaurantList.get(0).getId();
        } else {
            return 0;
        }
    }

    public boolean isFirstRestaurantLoyalty() {
        if (baseRestaurantList != null && baseRestaurantList.size() > 0) {
            return baseRestaurantList.get(0).isLoyaltyProgramEnable();
        } else {
            return false;
        }
    }

    public void initReservationRestaurantList() {
        reservationRestaurantList = new ArrayList<>();
        for (Restaurant restaurant : baseRestaurantList) {
            if (restaurant.getServices().isTableReservation()) {
                reservationRestaurantList.add(restaurant);
            }
        }
    }

    public boolean isAvailableReservationRest() {
        return reservationRestaurantList != null && !reservationRestaurantList.isEmpty();
    }

    public List<Restaurant> getDeliveryRestaurantList() {
        return deliveryRestaurantList;
    }

    public void setDeliveryRestaurantList(List<Restaurant> deliveryRestaurantList) {
        this.deliveryRestaurantList = deliveryRestaurantList;
    }

    public List<Restaurant> getBaseRestaurantList() {
        return baseRestaurantList;
    }

    public void setBaseRestaurantList(List<Restaurant> baseRestaurantList) {
        this.baseRestaurantList = baseRestaurantList;
    }

    public List<Restaurant> getReservationRestaurantList() {
        return reservationRestaurantList;
    }

    public void setReservationRestaurantList(List<Restaurant> reservationRestaurantList) {
        this.reservationRestaurantList = reservationRestaurantList;
    }

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }
}
