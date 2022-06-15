package app.client.munchbear.munchbearclient.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;

/**
 * @author Roman H.
 */

public class OrderLocation extends Address {

    @SerializedName("address")
    @Expose(serialize = false)
    private String address;

    @SerializedName("flat_number")
    @Expose(serialize = false)
    private String flatNumber;

    @SerializedName("table_number")
    @Expose(serialize = false)
    private Integer tableNumber;

    @SerializedName("restaurant")
    @Expose(serialize = false)
    private Restaurant restaurant;

    public OrderLocation(String address, String flatNumber, Double lat, Double lng) {
        super(lat, lng);
        this.address = address;
        this.flatNumber = flatNumber;
    }

    public OrderLocation(Integer table_number) {
        this.tableNumber = table_number;
    }

    public OrderLocation(Integer table_number, Restaurant restaurant) {
        this.tableNumber = table_number;
        this.restaurant = restaurant;
    }

    public OrderLocation(String street, String house, String unitNumber, String postalCode, String city,
                         String countryCode, String countryName, double lat, double lng) {
        super(street, house, unitNumber, postalCode, city, countryCode, countryName, lat, lng);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}