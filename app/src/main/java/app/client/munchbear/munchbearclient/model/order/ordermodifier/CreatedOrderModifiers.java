package app.client.munchbear.munchbearclient.model.order.ordermodifier;

import com.google.gson.annotations.SerializedName;

public class CreatedOrderModifiers {

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("total")
    private int total;

    @SerializedName("price")
    private int price;

    @SerializedName("modifier_type")
    private String modifier_type;

    @SerializedName("modifier")
    private CreatedOrderModifierItem modifierItem;

    public CreatedOrderModifiers(int quantity, int total, int price, String modifier_type, CreatedOrderModifierItem modifierItem) {
        this.quantity = quantity;
        this.total = total;
        this.price = price;
        this.modifier_type = modifier_type;
        this.modifierItem = modifierItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getModifier_type() {
        return modifier_type;
    }

    public void setModifier_type(String modifier_type) {
        this.modifier_type = modifier_type;
    }

    public CreatedOrderModifierItem getModifierItem() {
        return modifierItem;
    }

    public void setModifierItem(CreatedOrderModifierItem modifierItem) {
        this.modifierItem = modifierItem;
    }
}
