package app.client.munchbear.munchbearclient.model.modifier.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.model.modifier.MandatoryModifier;

/**
 * @author Roman H.
 */

public class MandatoryModifierCategory extends ModifierCategory implements Parcelable {

    @SerializedName("items")
    private List<MandatoryModifier> mandatoryModifierList;

    @SerializedName("max_amount")
    private int maxAmount;

    public MandatoryModifierCategory(int id, String name, List<MandatoryModifier> mandatoryModifierList, int maxAmount) {
        super(id, name);
        this.mandatoryModifierList = mandatoryModifierList;
        this.maxAmount = maxAmount;
    }

    public MandatoryModifierCategory(Parcel in) {
        super(in);
        mandatoryModifierList = in.createTypedArrayList(MandatoryModifier.CREATOR);
        maxAmount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(mandatoryModifierList);
        dest.writeInt(maxAmount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MandatoryModifierCategory> CREATOR = new Creator<MandatoryModifierCategory>() {
        @Override
        public MandatoryModifierCategory createFromParcel(Parcel in) {
            return new MandatoryModifierCategory(in);
        }

        @Override
        public MandatoryModifierCategory[] newArray(int size) {
            return new MandatoryModifierCategory[size];
        }
    };

    public List<MandatoryModifier> getMandatoryModifierList() {
        return mandatoryModifierList;
    }

    public void setMandatoryModifierList(List<MandatoryModifier> mandatoryModifierList) {
        this.mandatoryModifierList = mandatoryModifierList;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<MandatoryModifier> getSelectedList() {
        List<MandatoryModifier> modList = new ArrayList<>();
        for (MandatoryModifier modifier : mandatoryModifierList) {
            if (modifier.isSelected()) {
                modList.add(modifier);
            }
        }
        return modList;
    }
}
