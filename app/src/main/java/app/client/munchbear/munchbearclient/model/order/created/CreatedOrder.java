package app.client.munchbear.munchbearclient.model.order.created;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.concurrent.TimeUnit;

import app.client.munchbear.munchbearclient.model.order.OrderDelivery;
import app.client.munchbear.munchbearclient.model.order.OrderUserContact;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;

public class CreatedOrder implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private int status;

    @SerializedName("subtotal")
    private int subtotal;

    @SerializedName("delivery_fee")
    private int deliveryFee;

    @SerializedName("tax")
    private int tax;

    @SerializedName("total")
    private int total;

    @SerializedName("cooking_time")
    private int cookingTime;

    @SerializedName("cooking_started_at")
    private long cookingStartedAt;

    @SerializedName("created_at")
    private long created_at;

    @SerializedName("number")
    private int number;

    @SerializedName("contact")
    private OrderUserContact userContact;

    @SerializedName("comment")
    private String comment;

    @SerializedName("delivery")
    private OrderDelivery orderDelivery;

    public CreatedOrder() {

    }

    public CreatedOrder(String id, int status, OrderUserContact userContact, OrderDelivery orderDelivery, String comment, int subtotal,
                        int deliveryFee, int tax, int total, int cookingTime, long created_at, int number, long cookingStartedAt) {
        this.id = id;
        this.status = status;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.tax = tax;
        this.total = total;
        this.cookingTime = cookingTime;
        this.created_at = created_at;
        this.number = number;
        this.userContact = userContact;
        this.comment = comment;
        this.orderDelivery = orderDelivery;
        this.cookingStartedAt = cookingStartedAt;
    }

    protected CreatedOrder(Parcel in) {
        id = in.readString();
        status = in.readInt();
        subtotal = in.readInt();
        deliveryFee = in.readInt();
        tax = in.readInt();
        total = in.readInt();
        cookingTime = in.readInt();
        created_at = in.readLong();
        number = in.readInt();
        comment = in.readString();
        cookingStartedAt = in.readLong();
    }

    public static final Creator<CreatedOrder> CREATOR = new Creator<CreatedOrder>() {
        @Override
        public CreatedOrder createFromParcel(Parcel in) {
            return new CreatedOrder(in);
        }

        @Override
        public CreatedOrder[] newArray(int size) {
            return new CreatedOrder[size];
        }
    };

    /**
     * @return long time when order will be ready (cooked and delivered)
     */
    public long getReadyTime() {
        if (status == DeliveryVariant.ORDER_STATUS_COOKING || status == DeliveryVariant.ORDER_STATUS_IN_DELIVERY) {
            long endOfCooking = cookingStartedAt + TimeUnit.MINUTES.toSeconds(cookingTime);
            long millisToEnd = endOfCooking - System.currentTimeMillis() / 1000L;
            return TimeUnit.SECONDS.toMinutes(millisToEnd) - 1;
        } else {
            return 0;
        }
    }

    /**
     * @return String time when order will be ready (cooked and delivered)
     */
    public String getReadyTimeString(boolean showPostfix) {
        if (status == DeliveryVariant.ORDER_STATUS_COOKING || status == DeliveryVariant.ORDER_STATUS_IN_DELIVERY) {
            if (getReadyTime() > 0) {
                return getReadyTime()  + (showPostfix ? " mins" : "");
            } else {
                return "Late";
            }
        } else {
            return "";
        }
    }

    public boolean showReadyTime() {
        return (status == DeliveryVariant.ORDER_STATUS_COOKING || status == DeliveryVariant.ORDER_STATUS_IN_DELIVERY);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getTotal() {
        return total;
    }

    public double getTotalInDollar() {
        double totalDouble = (double) total;
        return totalDouble / 100;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public long getCookingStartedAt() {
        return cookingStartedAt;
    }

    public void setCookingStartedAt(long cookingStartedAt) {
        this.cookingStartedAt = cookingStartedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(status);
        parcel.writeInt(subtotal);
        parcel.writeInt(deliveryFee);
        parcel.writeInt(tax);
        parcel.writeInt(total);
        parcel.writeInt(cookingTime);
        parcel.writeLong(created_at);
        parcel.writeInt(number);
        parcel.writeString(comment);
        parcel.writeLong(cookingStartedAt);
    }
}
