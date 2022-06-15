package app.client.munchbear.munchbearclient.model.dish;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DishCategory implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("dishes")
    private List<Dish> dishList;

    private boolean isSelected;

    public DishCategory(int id, String name, String imageUrl, List<Dish> dishList, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.dishList = dishList;
        this.isSelected = isSelected;
    }

    protected DishCategory(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        dishList = in.createTypedArrayList(Dish.CREATOR);
        isSelected = in.readByte() != 0;
    }

    public static final Creator<DishCategory> CREATOR = new Creator<DishCategory>() {
        @Override
        public DishCategory createFromParcel(Parcel in) {
            return new DishCategory(in);
        }

        @Override
        public DishCategory[] newArray(int size) {
            return new DishCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Dish> getDishList() {
        return dishList;
    }

    public void setDishList(List<Dish> dishList) {
        this.dishList = dishList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(imageUrl);
        parcel.writeTypedList(dishList);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }
}
