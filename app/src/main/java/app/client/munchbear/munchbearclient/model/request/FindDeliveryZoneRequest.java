package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

public class FindDeliveryZoneRequest {

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public FindDeliveryZoneRequest(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

