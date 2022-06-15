package app.client.munchbear.munchbearclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class Address implements Parcelable{

    public static final String ADDRESS = "key.address";

    @SerializedName("lat")
    @Expose(serialize = false)
    private Double lat;

    @SerializedName("lng")
    @Expose(serialize = false)
    private Double lng;

    @SerializedName("city")
    @Expose(serialize = false)
    private String city;

    @SerializedName("state")
    @Expose(serialize = false)
    private String state;

    @SerializedName("street")
    @Expose(serialize = false)
    private String street;

    @SerializedName("country")
    @Expose(serialize = false)
    private String countryName;

    @SerializedName("zipcode")
    @Expose(serialize = false)
    private String postalCode;

    @SerializedName("street_number")
    @Expose(serialize = false)
    private String house;

    private String countryCode;
    private String unitNumber;

    public Address() {

    }

    public Address(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    protected Address(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        city = in.readString();
        state = in.readString();
        street = in.readString();
        countryName = in.readString();
        postalCode = in.readString();
        house = in.readString();
        countryCode = in.readString();
        unitNumber = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(street);
        dest.writeString(countryName);
        dest.writeString(postalCode);
        dest.writeString(house);
        dest.writeString(countryCode);
        dest.writeString(unitNumber);
    }

    public Address(String street, String house, String unitNumber, String postalCode, String city,
                   String countryCode, String countryName, Double lat, Double lng) {
        this.street = street;
        this.house = house;
        this.unitNumber = unitNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getFullAddress() {
        return house + " " + street + ". " + city + ". " + countryName;
    }

    public String getAddressForCourier() {
        return city + " " + street + " " + house + " " + unitNumber;
    }

}

