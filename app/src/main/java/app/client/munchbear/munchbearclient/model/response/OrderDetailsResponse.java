package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.order.OrderDetails;

public class OrderDetailsResponse {

    @SerializedName("data")
    private OrderDetails orderDetails;

    public OrderDetailsResponse(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
}
