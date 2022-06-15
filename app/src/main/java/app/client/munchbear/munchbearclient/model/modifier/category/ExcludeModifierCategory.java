package app.client.munchbear.munchbearclient.model.modifier.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.model.modifier.ExcludeModifier;

/**
 * @author Roman H.
 */

public class ExcludeModifierCategory extends ModifierCategory implements Parcelable {

    @SerializedName("items")
    private List<ExcludeModifier> excludeModifierList;

    public ExcludeModifierCategory(int id, String name, List<ExcludeModifier> excludeModifierList) {
        super(id, name);
        this.excludeModifierList = excludeModifierList;
    }

    public ExcludeModifierCategory(Parcel in) {
        super(in);
        excludeModifierList = in.createTypedArrayList(ExcludeModifier.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(excludeModifierList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExcludeModifierCategory> CREATOR = new Creator<ExcludeModifierCategory>() {
        @Override
        public ExcludeModifierCategory createFromParcel(Parcel in) {
            return new ExcludeModifierCategory(in);
        }

        @Override
        public ExcludeModifierCategory[] newArray(int size) {
            return new ExcludeModifierCategory[size];
        }
    };

    public List<ExcludeModifier> getExcludeModifierList() {
        return excludeModifierList;
    }

    public void setExcludeModifierList(List<ExcludeModifier> excludeModifierList) {
        this.excludeModifierList = excludeModifierList;
    }

    public List<ExcludeModifier> getSelectedList() {
        List<ExcludeModifier> modList = new ArrayList<>();
        for (ExcludeModifier modifier : excludeModifierList) {
            if (modifier.isSelected()) {
                modList.add(modifier);
            }
        }
        return modList;
    }
}
