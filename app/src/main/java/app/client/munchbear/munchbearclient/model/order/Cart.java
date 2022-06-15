package app.client.munchbear.munchbearclient.model.order;

import java.util.List;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.restaurant.DeliveryZone;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;

public class Cart {

    private static Cart cartInstance;

    private boolean cartEmpty = true;
    private double totalPriceDollar = 0.00;
    private int totalPricePoint = 0;
    private List<Dish> dishList;
    private int selectedDeliveryType = 0;
    private Restaurant selectedRestaurant;
    private Address selectedDeliveryAddress;
    private DeliveryZone selectedDeliveryZone;

    private Cart() {

    }

    public static Cart getInstance() {
        if (cartInstance == null) {
            cartInstance = new Cart();
        }
        return cartInstance;
    }

    public void updateCartPrices() {
        totalPriceDollar = 0.00;
        totalPricePoint = 0;

        for (Dish dish: dishList) {
            totalPriceDollar = totalPriceDollar + (dish.getCostDollar() * dish.getCount());
            totalPricePoint = totalPricePoint + (dish.getCostPoint() * dish.getCount());
        }
    }

    public boolean isTotalSumMore() {
        return totalPriceDollar >= selectedDeliveryZone.getMinOrderInDollar();
    }

    public static void clearCart() {
        cartInstance = new Cart();
    }

    public boolean isCartEmpty() {
        return cartEmpty;
    }

    public void setCartEmpty(boolean cartEmpty) {
        this.cartEmpty = cartEmpty;
    }

    public double getTotalPriceDollar() {
        return totalPriceDollar;
    }

    public void setTotalPriceDollar(double totalPriceDollar) {
        this.totalPriceDollar = totalPriceDollar;
    }

    public int getTotalPricePoint() {
        return totalPricePoint;
    }

    public void setTotalPricePoint(int totalPricePoint) {
        this.totalPricePoint = totalPricePoint;
    }

    public List<Dish> getDishList() {
        return dishList;
    }

    public void setDishList(List<Dish> dishList) {
        this.dishList = dishList;
    }

    public int getSelectedDeliveryType() {
        return selectedDeliveryType;
    }

    public void setSelectedDeliveryType(int selectedDeliveryType) {
        this.selectedDeliveryType = selectedDeliveryType;
    }

    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public void setSelectedRestaurant(Restaurant selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }

    public Address getSelectedDeliveryAddress() {
        return selectedDeliveryAddress;
    }

    public void setSelectedDeliveryAddress(Address selectedDeliveryAddress) {
        this.selectedDeliveryAddress = selectedDeliveryAddress;
    }

    public DeliveryZone getSelectedDeliveryZone() {
        return selectedDeliveryZone;
    }

    public void setSelectedDeliveryZone(DeliveryZone selectedDeliveryZone) {
        this.selectedDeliveryZone = selectedDeliveryZone;
    }
}
