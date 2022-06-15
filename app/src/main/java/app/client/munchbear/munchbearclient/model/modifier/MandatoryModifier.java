package app.client.munchbear.munchbearclient.model.modifier;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class MandatoryModifier extends Modifier implements Parcelable {

    @SerializedName("price")
    protected int priceCent;

    @SerializedName("loyalty_price")
    protected int pricePoint;

    public MandatoryModifier(int id, String name, boolean isSelected, int priceCent, int pricePoint) {
        super(id, name, isSelected);
        this.priceCent = priceCent;
        this.pricePoint = pricePoint;
    }

    public MandatoryModifier(Parcel in) {
        super(in);
        priceCent = in.readInt();
        pricePoint = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(priceCent);
        dest.writeInt(pricePoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MandatoryModifier> CREATOR = new Creator<MandatoryModifier>() {
        @Override
        public MandatoryModifier createFromParcel(Parcel in) {
            return new MandatoryModifier(in);
        }

        @Override
        public MandatoryModifier[] newArray(int size) {
            return new MandatoryModifier[size];
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

    public double getPriceInDollar() {
        double p = priceCent;
        return p/100;
    }
}
