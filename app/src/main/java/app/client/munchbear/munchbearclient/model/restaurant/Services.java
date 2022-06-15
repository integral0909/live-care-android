package app.client.munchbear.munchbearclient.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class Services implements Parcelable {

    @SerializedName("eat_in")
    private boolean eatIn;

    @SerializedName("pick_up")
    private boolean pickUp;

    @SerializedName("delivery")
    private boolean delivery;

    @SerializedName("order_ahead")
    private boolean orderAhead;

    @SerializedName("table_reservation")
    private boolean tableReservation;

    public Services() {

    }

    public Services(boolean eatIn, boolean pickUp, boolean delivery, boolean orderAhead, boolean tableReservation) {
        this.eatIn = eatIn;
        this.pickUp = pickUp;
        this.delivery = delivery;
        this.orderAhead = orderAhead;
        this.tableReservation = tableReservation;
    }

    protected Services(Parcel in) {
        eatIn = in.readByte() != 0;
        pickUp = in.readByte() != 0;
        delivery = in.readByte() != 0;
        orderAhead = in.readByte() != 0;
        tableReservation = in.readByte() != 0;
    }

    public static final Creator<Services> CREATOR = new Creator<Services>() {
        @Override
        public Services createFromParcel(Parcel in) {
            return new Services(in);
        }

        @Override
        public Services[] newArray(int size) {
            return new Services[size];
        }
    };

    public boolean isEatIn() {
        return eatIn;
    }

    public void setEatIn(boolean eatIn) {
        this.eatIn = eatIn;
    }

    public boolean isPickUp() {
        return pickUp;
    }

    public void setPickUp(boolean pickUp) {
        this.pickUp = pickUp;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public boolean isOrderAhead() {
        return orderAhead;
    }

    public void setOrderAhead(boolean orderAhead) {
        this.orderAhead = orderAhead;
    }

    public boolean isTableReservation() {
        return tableReservation;
    }

    public void setTableReservation(boolean tableReservation) {
        this.tableReservation = tableReservation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (eatIn ? 1 : 0));
        dest.writeByte((byte) (pickUp ? 1 : 0));
        dest.writeByte((byte) (delivery ? 1 : 0));
        dest.writeByte((byte) (orderAhead ? 1 : 0));
        dest.writeByte((byte) (tableReservation ? 1 : 0));
    }
}
