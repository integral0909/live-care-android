package app.client.munchbear.munchbearclient.model.dish;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.modifier.ExcludeModifier;
import app.client.munchbear.munchbearclient.model.modifier.Modifier;
import app.client.munchbear.munchbearclient.model.modifier.category.ExcludeModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.MandatoryModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.OptionalModifierCategory;
import app.client.munchbear.munchbearclient.utils.DishHelper;

public class Dish implements Parcelable, Cloneable{

    public static final String KEY_DISH = "key.dish";
    public static final String KEY_DISH_EDIT = "key.dish.edit";
    public static final String KEY_DISH_EDIT_POSITION = "key.dish.edit.position";

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String imgUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("sizes")
    private List<DishSize> dishSizeList;

    @SerializedName("exclude_groups")
    private List<ExcludeModifierCategory> excludeModifierList;

    @SerializedName("optional_groups")
    private List<OptionalModifierCategory> optionalModifierList;

    @SerializedName("mandatory_groups")
    private List<MandatoryModifierCategory> mandatoryModifierList;

    private double costDollar;
    private int costPoint;
    private int count = 1;

    public Dish() {

    }

    public Dish(int id, String name, String imageUrl, String description, List<DishSize> dishSizes,
                List<ExcludeModifierCategory> excludeModifierList, List<OptionalModifierCategory> optionalModifierList,
                List<MandatoryModifierCategory> mandatoryModifierList) {
        this.id = id;
        this.name = name;
        this.imgUrl = imageUrl;
        this.description = description;
        this.dishSizeList = dishSizes;
        this.excludeModifierList = excludeModifierList;
        this.optionalModifierList = optionalModifierList;
        this.mandatoryModifierList = mandatoryModifierList;
    }

    protected Dish(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imgUrl = in.readString();
        description = in.readString();
        count = in.readInt();
        costDollar = in.readDouble();
        costPoint = in.readInt();
        dishSizeList = in.createTypedArrayList(DishSize.CREATOR);
        excludeModifierList = in.createTypedArrayList(ExcludeModifierCategory.CREATOR);
        optionalModifierList = in.createTypedArrayList(OptionalModifierCategory.CREATOR);
        mandatoryModifierList = in.createTypedArrayList(MandatoryModifierCategory.CREATOR);
    }

    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DishSize> getDishSizeList() {
        return dishSizeList;
    }

    public void setDishSizeList(List<DishSize> dishSizeList) {
        this.dishSizeList = dishSizeList;
    }

    public List<ExcludeModifierCategory> getExcludeModifierList() {
        return excludeModifierList;
    }

    public void setExcludeModifierList(List<ExcludeModifierCategory> excludeModifierList) {
        this.excludeModifierList = excludeModifierList;
    }

    public List<OptionalModifierCategory> getOptionalModifierList() {
        return optionalModifierList;
    }

    public void setOptionalModifierList(List<OptionalModifierCategory> optionalModifierList) {
        this.optionalModifierList = optionalModifierList;
    }

    public List<MandatoryModifierCategory> getMandatoryModifierList() {
        return mandatoryModifierList;
    }

    public void setMandatoryModifierList(List<MandatoryModifierCategory> mandatoryModifierList) {
        this.mandatoryModifierList = mandatoryModifierList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ExcludeModifierCategory> getAllExcludeInOneList(Context context) {
        List<ExcludeModifierCategory> excludeCategoryList = new ArrayList<>();
        List<ExcludeModifier> allExcludeList = new ArrayList<>();
        String excludeTitle = context.getResources().getString(R.string.detail_dish_exclude);

        for (ExcludeModifierCategory category : excludeModifierList) {
            allExcludeList.addAll(category.getExcludeModifierList());
        }

        ExcludeModifierCategory allExcludeCategory = new ExcludeModifierCategory(1, excludeTitle, allExcludeList);
        excludeCategoryList.add(allExcludeCategory);

        return excludeCategoryList;

    }

    public double getCostDollar() {
        double cost = 0.00;
        if (costDollar != 0.00) {
            cost = costDollar;
        } else if (!dishSizeList.isEmpty()) {
            for (DishSize dishSize : dishSizeList) {
                if (dishSize.isSelected()) {
                    cost = dishSize.getPriceInDouble(true);
                }
            }
        }

        cost = DishHelper.getDollarCostFromOptionalModifiers(cost, optionalModifierList);
        cost = DishHelper.getDollarCostFromMandatoryModifiers(cost, mandatoryModifierList);

        return cost;
    }

    public int getCostPoint() {
        int cost = 0;
        if (costPoint != 0.00) {
            cost = costPoint;
        } else if (!dishSizeList.isEmpty()) {
            for (DishSize dishSize : dishSizeList) {
                if (dishSize.isSelected()) {
                    cost = dishSize.getPricePoint();
                }
            }
        }

        cost = DishHelper.getPointCostFromOptionalModifiers(cost, optionalModifierList);
        cost = DishHelper.getPointCostFromMandatoryModifiers(cost, mandatoryModifierList);

        return cost;
    }

    public void setCostPoint(int costPoint) {
        this.costPoint = costPoint;
    }

    public void setCostDollar(double costDollar) {
        this.costDollar = costDollar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(imgUrl);
        parcel.writeString(description);
        parcel.writeInt(count);
        parcel.writeDouble(costDollar);
        parcel.writeInt(costPoint);
        parcel.writeTypedList(dishSizeList);
        parcel.writeTypedList(excludeModifierList);
        parcel.writeTypedList(optionalModifierList);
        parcel.writeTypedList(mandatoryModifierList);
    }

    public static Dish clone(Dish dish){
        Gson gson = new Gson();
        String s = gson.toJson(dish);
        return gson.fromJson(s, dish.getClass());
    }

    public DishSize getSelectedDishSize() {
        DishSize dishSize = new DishSize();
        if (!dishSizeList.isEmpty()) {
            for (DishSize dSize : dishSizeList) {
                if (dSize.isSelected()) {
                    dishSize = dSize;
                }
            }
        }
        return dishSize;
    }

    public String getMandatoryModifierTitles(Context context) {
        Resources resources = context.getResources();
        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(DishHelper.getMandatoryModifierTitles(resources, mandatoryModifierList));

        return titleBuilder.toString();
    }

    public String getModifiersTitle(Context context, boolean isExclude) {
        Resources resources = context.getResources();
        StringBuilder stringBuilder = new StringBuilder();

        String titles = isExclude ? DishHelper.getSelectedExcludeModifierTitle(resources, getAllExcludeInOneList(context))
                : DishHelper.getSelectedOptionalModifierTitle(resources, optionalModifierList);
        stringBuilder.append(titles);

        return stringBuilder.toString();
    }


    public List<Modifier> getSelectedModifierRecursion(boolean isExclude) {
        List<Modifier> modifierList = new ArrayList<>();
        modifierList.addAll(isExclude ? DishHelper.getSelectedExclude(excludeModifierList)
                : DishHelper.getAllSelectedOptional(optionalModifierList));

        return modifierList;
    }

}
