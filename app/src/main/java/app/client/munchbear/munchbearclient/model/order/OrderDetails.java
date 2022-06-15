package app.client.munchbear.munchbearclient.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.order.created.CreatedOrder;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrderItems;
import app.client.munchbear.munchbearclient.model.user.UserData;

public class OrderDetails extends CreatedOrder implements Parcelable {

    @SerializedName("customer")
    private UserData customer;

    @SerializedName("items")
    private List<CreatedOrderItems> createdOrderItemsList;

    @SerializedName("payment_type")
    private String paymentType;

    public OrderDetails() {

    }

    public OrderDetails(String id, int status, OrderUserContact userContact,  String paymentType, OrderDelivery orderDelivery, String comment, int subtotal, int deliveryFee, int tax, int total, long cookingStartedAt, List<CreatedOrderItems> createdOrderItemsList, int cookingTime, long created_at, int number, UserData customer) {
        super(id, status, userContact, orderDelivery, comment, subtotal, deliveryFee, tax, total, cookingTime, created_at, number, cookingStartedAt);
        this.customer = customer;
        this.paymentType = paymentType;
    }


    protected OrderDetails(Parcel in) {
        super(in);
        paymentType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(paymentType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDetails> CREATOR = new Creator<OrderDetails>() {
        @Override
        public OrderDetails createFromParcel(Parcel in) {
            return new OrderDetails(in);
        }

        @Override
        public OrderDetails[] newArray(int size) {
            return new OrderDetails[size];
        }
    };

    public UserData getCustomer() {
        return customer;
    }

    public void setCustomer(UserData customer) {
        this.customer = customer;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<CreatedOrderItems> getCreatedOrderItemsList() {
        return createdOrderItemsList;
    }

    public void setCreatedOrderItemsList(List<CreatedOrderItems> createdOrderItemsList) {
        this.createdOrderItemsList = createdOrderItemsList;
    }
}
