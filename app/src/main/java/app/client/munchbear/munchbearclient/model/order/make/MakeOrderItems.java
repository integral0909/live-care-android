package app.client.munchbear.munchbearclient.model.order.make;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.order.OrderModifiers;

public class MakeOrderItems {

    @SerializedName("id")
    private int sizeId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("modifiers")
    private OrderModifiers orderModifiers;

    public MakeOrderItems(int sizeId, int quantity, OrderModifiers orderModifiers) {
        this.sizeId = sizeId;
        this.quantity = quantity;
        this.orderModifiers = orderModifiers;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderModifiers getOrderModifiers() {
        return orderModifiers;
    }

    public void setOrderModifiers(OrderModifiers orderModifiers) {
        this.orderModifiers = orderModifiers;
    }
}
