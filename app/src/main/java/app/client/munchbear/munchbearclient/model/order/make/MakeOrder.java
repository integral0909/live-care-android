package app.client.munchbear.munchbearclient.model.order.make;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.order.OrderDelivery;
import app.client.munchbear.munchbearclient.model.order.OrderUserContact;

/**
 * @author Roman H.
 */

public class MakeOrder {

    public static final String ORDERS_LIST = "key.order.list";
    public static final String ORDERS = "key.orders";
    public static final String ORDER_ID = "key.order.id";

    @SerializedName("contact")
    private OrderUserContact userContact;

    @SerializedName("comment")
    private String comment;

    @SerializedName("delivery")
    private OrderDelivery orderDelivery;

    @SerializedName("payment_type")
    private String paymentType;

    @SerializedName("items")
    private List<MakeOrderItems> makeOrderItemsList;

    public MakeOrder() {

    }

    public MakeOrder(OrderUserContact userContact, String comment, String paymentType) {
        this.userContact = userContact;
        this.comment = comment;
        this.paymentType = paymentType;
    }

    public MakeOrder(OrderUserContact userContact, String comment, String paymentType, OrderDelivery orderDelivery, List<MakeOrderItems> makeOrderItemsList) {
        this.userContact = userContact;
        this.comment = comment;
        this.paymentType = paymentType;
        this.orderDelivery = orderDelivery;
        this.makeOrderItemsList = makeOrderItemsList;
    }

    public OrderUserContact getUserContact() {
        return userContact;
    }

    public void setUserContact(OrderUserContact userContact) {
        this.userContact = userContact;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrderDelivery getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(OrderDelivery orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<MakeOrderItems> getMakeOrderItemsList() {
        return makeOrderItemsList;
    }

    public void setMakeOrderItemsList(List<MakeOrderItems> makeOrderItemsList) {
        this.makeOrderItemsList = makeOrderItemsList;
    }
}
