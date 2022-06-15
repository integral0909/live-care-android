package app.client.munchbear.munchbearclient.model.modifier;

/**
 * @author Roman H.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Modifier implements Parcelable {

    @SerializedName("id")
    protected int id;

    @SerializedName("name")
    protected String name;

    protected boolean isSelected;

    public Modifier(int id, String name, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public Modifier(Parcel in) {
        id = in.readInt();
        name = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Modifier> CREATOR = new Creator<Modifier>() {
        @Override
        public Modifier createFromParcel(Parcel in) {
            return new Modifier(in);
        }

        @Override
        public Modifier[] newArray(int size) {
            return new Modifier[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

}

