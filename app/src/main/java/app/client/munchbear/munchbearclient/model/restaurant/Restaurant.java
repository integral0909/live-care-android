package app.client.munchbear.munchbearclient.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.Address;

/**
 * @author Roman H.
 */

public class Restaurant implements Parcelable {

    public static final String RESTAURANT = "key.restaurant";

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("logo")
    private String logoUrl;

    @SerializedName("location")
    private Address address;

    @SerializedName("delivery_zones")
    private List<DeliveryZone> deliveryZoneList;

    @SerializedName("service_hours_today")
    private WorkingHours workingHours;

    @SerializedName("services")
    private Services services;

    @SerializedName("loyalty_program_enabled")
    private boolean loyaltyProgramEnable;

    public Restaurant() {

    }

    public Restaurant(int id) {
        this.id = id;
    }

    public Restaurant(int id, String name, String logoUrl, Address address, List<DeliveryZone> deliveryZoneList,
                      WorkingHours workingHours, Services services, boolean loyaltyProgramEnable) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.address = address;
        this.deliveryZoneList = deliveryZoneList;
        this.workingHours = workingHours;
        this.services = services;
        this.loyaltyProgramEnable  = loyaltyProgramEnable;
    }

    protected Restaurant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        logoUrl = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        deliveryZoneList = in.createTypedArrayList(DeliveryZone.CREATOR);
        workingHours = in.readParcelable(WorkingHours.class.getClassLoader());
        services = in.readParcelable(Services.class.getClassLoader());
        loyaltyProgramEnable = in.readByte() != 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(logoUrl);
        dest.writeParcelable(address, flags);
        dest.writeTypedList(deliveryZoneList);
        dest.writeParcelable(workingHours, flags);
        dest.writeParcelable(services, flags);
        dest.writeByte((byte) (loyaltyProgramEnable ? 1 : 0));
    }

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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Address getAddress() {
        return address;
    }

    public void setLocation(Address location) {
        this.address = location;
    }

    public List<DeliveryZone> getDeliveryZoneList() {
        return deliveryZoneList;
    }

    public void setDeliveryZoneList(List<DeliveryZone> deliveryZoneList) {
        this.deliveryZoneList = deliveryZoneList;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public boolean isLoyaltyProgramEnable() {
        return loyaltyProgramEnable;
    }

    public void setLoyaltyProgramEnable(boolean loyaltyProgramEnable) {
        this.loyaltyProgramEnable = loyaltyProgramEnable;
    }
}
