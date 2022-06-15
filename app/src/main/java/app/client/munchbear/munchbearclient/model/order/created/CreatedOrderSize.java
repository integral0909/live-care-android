package app.client.munchbear.munchbearclient.model.order.created;

import com.google.gson.annotations.SerializedName;

public class CreatedOrderSize {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private int price;

    @SerializedName("dish")
    private CreatedOrderDish createdOrderDish;

    public CreatedOrderSize(int id, String name, int price, CreatedOrderDish createdOrderDish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.createdOrderDish = createdOrderDish;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public CreatedOrderDish getCreatedOrderDish() {
        return createdOrderDish;
    }

    public void setCreatedOrderDish(CreatedOrderDish createdOrderDish) {
        this.createdOrderDish = createdOrderDish;
    }
}
