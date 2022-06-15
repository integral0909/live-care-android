package app.client.munchbear.munchbearclient.model.modifier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Roman H.
 */

public class ExcludeModifier extends Modifier implements Parcelable {

    //In future can be fields for only this type of modifier

    public ExcludeModifier(int id, String name, boolean isSelected) {
        super(id, name, isSelected);
    }

    public ExcludeModifier(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExcludeModifier> CREATOR = new Creator<ExcludeModifier>() {
        @Override
        public ExcludeModifier createFromParcel(Parcel in) {
            return new ExcludeModifier(in);
        }

        @Override
        public ExcludeModifier[] newArray(int size) {
            return new ExcludeModifier[size];
        }
    };

}
