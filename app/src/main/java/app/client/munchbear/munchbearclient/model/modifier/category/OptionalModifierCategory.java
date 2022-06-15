package app.client.munchbear.munchbearclient.model.modifier.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.model.modifier.OptionalModifier;

/**
 * @author Roman H.
 */

public class OptionalModifierCategory extends ModifierCategory implements Parcelable {

    @SerializedName("items")
    private List<OptionalModifier> optionalModifierList;

    public OptionalModifierCategory(int id, String name, List<OptionalModifier> optionalModifierList) {
        super(id, name);
        this.optionalModifierList = optionalModifierList;
    }

    public OptionalModifierCategory(Parcel in) {
        super(in);
        optionalModifierList = in.createTypedArrayList(OptionalModifier.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(optionalModifierList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OptionalModifierCategory> CREATOR = new Creator<OptionalModifierCategory>() {
        @Override
        public OptionalModifierCategory createFromParcel(Parcel in) {
            return new OptionalModifierCategory(in);
        }

        @Override
        public OptionalModifierCategory[] newArray(int size) {
            return new OptionalModifierCategory[size];
        }
    };

    public List<OptionalModifier> getOptionalModifierList() {
        return optionalModifierList;
    }

    public void setOptionalModifierList(List<OptionalModifier> optionalModifierList) {
        this.optionalModifierList = optionalModifierList;
    }

    public List<OptionalModifier> getSelectedList() {
        List<OptionalModifier> modList = new ArrayList<>();
        for (OptionalModifier modifier : optionalModifierList) {
            if (modifier.isSelected()) {
                modList.add(modifier);
            }
        }
        return modList;
    }
}
