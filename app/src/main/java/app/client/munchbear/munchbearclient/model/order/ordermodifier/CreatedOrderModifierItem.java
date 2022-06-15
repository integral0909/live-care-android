package app.client.munchbear.munchbearclient.model.order.ordermodifier;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedOrderModifierItem {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @Expose(serialize = false)
    @SerializedName("price")
    private Integer price;

    public CreatedOrderModifierItem(int id, String name, String type, Integer price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
