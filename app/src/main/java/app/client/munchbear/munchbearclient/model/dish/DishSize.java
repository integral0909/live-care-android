package app.client.munchbear.munchbearclient.model.dish;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DishSize implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String sizeTitle;

    @SerializedName("price")
    private int priceCent;

    @SerializedName("loyalty_price")
    private int pricePoint;

    private boolean selected;

    public DishSize() {

    }

    public DishSize(int id, String title, int priceCent, int pricePoint, boolean selected) {
        this.id = id;
        this.sizeTitle = title;
        this.priceCent = priceCent;
        this.pricePoint = pricePoint;
        this.selected = selected;
    }

    protected DishSize(Parcel in) {
        id = in.readInt();
        sizeTitle = in.readString();
        priceCent = in.readInt();
        pricePoint = in.readInt();
        selected = in.readByte() != 0;
    }

    public static final Creator<DishSize> CREATOR = new Creator<DishSize>() {
        @Override
        public DishSize createFromParcel(Parcel in) {
            return new DishSize(in);
        }

        @Override
        public DishSize[] newArray(int size) {
            return new DishSize[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSizeTitle() {
        return sizeTitle;
    }

    public void setSizeTitle(String sizeTitle) {
        this.sizeTitle = sizeTitle;
    }

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

    public double getPriceInDouble(boolean inDollar) {
        double p;
        if (inDollar) {
            p = priceCent;
        } else {
            p = pricePoint;
        }
        return p/100;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sizeTitle);
        parcel.writeInt(priceCent);
        parcel.writeInt(id);
        parcel.writeInt(pricePoint);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
