package app.client.munchbear.munchbearclient.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class DeliveryZone implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("fee")
    private int fee;

    @SerializedName("radius")
    private int radius;

    @SerializedName("minimum_order")
    private double minOrder;

    @SerializedName("use_global_hours")
    private boolean useGlobalHours;

    public DeliveryZone() {

    }

    public DeliveryZone(int id, double lat, double lng, int fee, int radius, double minOrder, boolean useGlobalHours) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.fee = fee;
        this.radius = radius;
        this.minOrder = minOrder;
        this.useGlobalHours = useGlobalHours;
    }

    protected DeliveryZone(Parcel in) {
        id = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
        fee = in.readInt();
        radius = in.readInt();
        minOrder = in.readDouble();
        useGlobalHours = in.readByte() != 0;
    }

    public static final Creator<DeliveryZone> CREATOR = new Creator<DeliveryZone>() {
        @Override
        public DeliveryZone createFromParcel(Parcel in) {
            return new DeliveryZone(in);
        }

        @Override
        public DeliveryZone[] newArray(int size) {
            return new DeliveryZone[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(fee);
        dest.writeInt(radius);
        dest.writeDouble(minOrder);
        dest.writeByte((byte) (useGlobalHours ? 1 : 0));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getMinOrder() {
        return minOrder;
    }

    public double getMinOrderInDollar() {
        return minOrder / 100;
    }

    public void setMinOrder(double minOrder) {
        this.minOrder = minOrder;
    }

    public boolean isUseGlobalHours() {
        return useGlobalHours;
    }

    public void setUseGlobalHours(boolean useGlobalHours) {
        this.useGlobalHours = useGlobalHours;
    }
}
