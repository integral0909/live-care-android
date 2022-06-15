package app.client.munchbear.munchbearclient.model.modifier;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class OptionalModifier extends Modifier implements Parcelable {

    @SerializedName("price")
    protected int priceCent;

    @SerializedName("loyalty_price")
    protected int pricePoint;

    private int counter;

    public OptionalModifier(int id, String name, boolean isSelected, int priceCent, int pricePoint, int counter) {
        super(id, name, isSelected);
        this.priceCent = priceCent;
        this.pricePoint = pricePoint;
        this.counter = counter;
    }

    public OptionalModifier(Parcel in) {
        super(in);
        priceCent = in.readInt();
        pricePoint = in.readInt();
        counter = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(priceCent);
        dest.writeInt(pricePoint);
        dest.writeInt(counter);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OptionalModifier> CREATOR = new Creator<OptionalModifier>() {
        @Override
        public OptionalModifier createFromParcel(Parcel in) {
            return new OptionalModifier(in);
        }

        @Override
        public OptionalModifier[] newArray(int size) {
            return new OptionalModifier[size];
        }
    };

    public int getPriceCent() {
        return priceCent;
    }

    public void setPriceCent(int priceCent) {
        this.priceCent = priceCent;
    }

    public int getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(int pricePoint) {
        this.pricePoint = pricePoint;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public double getPriceInDollar(boolean total) {
        double p = total ? counter * priceCent : priceCent;
        return p/100;
    }
}
