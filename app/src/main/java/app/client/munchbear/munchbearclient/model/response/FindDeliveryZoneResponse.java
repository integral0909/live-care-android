package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.restaurant.DeliveryZone;

public class FindDeliveryZoneResponse {

    @SerializedName("data")
    DeliveryZone deliveryZone;

    public FindDeliveryZoneResponse(DeliveryZone deliveryZone) {
        this.deliveryZone = deliveryZone;
    }

    public DeliveryZone getDeliveryZone() {
        return deliveryZone;
    }

    public void setDeliveryZone(DeliveryZone deliveryZone) {
        this.deliveryZone = deliveryZone;
    }
}
