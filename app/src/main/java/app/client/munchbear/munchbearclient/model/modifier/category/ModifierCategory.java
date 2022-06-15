package app.client.munchbear.munchbearclient.model.modifier.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class ModifierCategory implements Parcelable {

    @SerializedName("id")
    protected int id;

    @SerializedName("name")
    protected String name;

    public ModifierCategory() {

    }

    public ModifierCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected ModifierCategory(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<ModifierCategory> CREATOR = new Creator<ModifierCategory>() {
        @Override
        public ModifierCategory createFromParcel(Parcel in) {
            return new ModifierCategory(in);
        }

        @Override
        public ModifierCategory[] newArray(int size) {
            return new ModifierCategory[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
