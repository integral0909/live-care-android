package app.client.munchbear.munchbearclient.model.menu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.dish.DishCategory;

/**
 * @author Roman H.
 */

public class RestaurantMenu {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("categories")
    private List<DishCategory> dishCategoryList;

    public RestaurantMenu(int id, String name, List<DishCategory> dishCategoryList) {
        this.id = id;
        this.name = name;
        this.dishCategoryList = dishCategoryList;
    }

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

    public List<DishCategory> getDishCategoryList() {
        return dishCategoryList;
    }

    public void setDishCategoryList(List<DishCategory> dishCategoryList) {
        this.dishCategoryList = dishCategoryList;
    }

}
