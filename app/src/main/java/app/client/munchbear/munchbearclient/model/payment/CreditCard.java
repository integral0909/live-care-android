package app.client.munchbear.munchbearclient.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CreditCard implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("brand")
    private String brand;

    @SerializedName("exp_year")
    private int expYear;

    @SerializedName("exp_month")
    private int expMonth;

    @SerializedName("last_four")
    private String last_four;

    @SerializedName("is_default")
    private boolean isDefault;

    public CreditCard(int id, String brand, int expYear, int expMonth, String last_four, boolean isDefault) {
        this.id = id;
        this.brand = brand;
        this.expYear = expYear;
        this.expMonth = expMonth;
        this.last_four = last_four;
        this.isDefault = isDefault;
    }

    protected CreditCard(Parcel in) {
        id = in.readInt();
        brand = in.readString();
        expYear = in.readInt();
        expMonth = in.readInt();
        last_four = in.readString();
        isDefault = in.readByte() != 0;
    }

    public static final Creator<CreditCard> CREATOR = new Creator<CreditCard>() {
        @Override
        public CreditCard createFromParcel(Parcel in) {
            return new CreditCard(in);
        }

        @Override
        public CreditCard[] newArray(int size) {
            return new CreditCard[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public String getLast_four() {
        return last_four;
    }

    public void setLast_four(String last_four) {
        this.last_four = last_four;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(brand);
        dest.writeInt(expYear);
        dest.writeInt(expMonth);
        dest.writeString(last_four);
        dest.writeByte((byte) (isDefault ? 1 : 0));
    }
}

