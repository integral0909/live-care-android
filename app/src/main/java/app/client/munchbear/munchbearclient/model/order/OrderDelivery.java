package app.client.munchbear.munchbearclient.model.order;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.utils.DeliveryVariant;

/**
 * @author Roman H.
 */

public class OrderDelivery {

    @SerializedName("type")
    private String type;

    @SerializedName("time")
    private String time;

    @SerializedName("location")
    private OrderLocation orderLocation;


    public OrderDelivery(String type, String time) {
        this.type = type;
        this.time = time;
    }

    public OrderDelivery(String type, String time, OrderLocation orderLocation) {
        this.type = type;
        this.time = time;
        this.orderLocation = orderLocation;
    }

    public LatLng getLatLng() {
        if (type.equals(DeliveryVariant.DELIVERY)) {
            return new LatLng(orderLocation.getLat(), orderLocation.getLng());
        } else if (type.equals(DeliveryVariant.EAT_IN)){
            return new LatLng(orderLocation.getRestaurant().getAddress().getLat(), orderLocation.getRestaurant().getAddress().getLng());
        } else {
            return new LatLng(orderLocation.getLat(), orderLocation.getLng());
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public OrderLocation getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(OrderLocation orderLocation) {
        this.orderLocation = orderLocation;
    }
}
