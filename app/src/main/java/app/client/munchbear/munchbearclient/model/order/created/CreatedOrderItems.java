package app.client.munchbear.munchbearclient.model.order.created;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.order.ordermodifier.CreatedOrderModifiers;

public class CreatedOrderItems {

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private int price;

    @SerializedName("total")
    private int total;

    @SerializedName("size")
    private CreatedOrderSize createdOrderSize;

    @SerializedName("exclude_modifiers")
    private List<CreatedOrderModifiers> orderExcludeModifiersList;

    @SerializedName("optional_modifiers")
    private List<CreatedOrderModifiers> orderOptionalModifiersList;

    @SerializedName("mandatory_modifiers")
    private List<CreatedOrderModifiers> orderMandatoryModifiersList;

    public CreatedOrderItems(int quantity, int price, int total, CreatedOrderSize createdOrderSize, List<CreatedOrderModifiers> orderExcludeModifiersList,
                             List<CreatedOrderModifiers> orderOptionalModifiersList, List<CreatedOrderModifiers> orderMandatoryModifiersList) {
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.createdOrderSize = createdOrderSize;
        this.orderExcludeModifiersList = orderExcludeModifiersList;
        this.orderOptionalModifiersList = orderOptionalModifiersList;
        this.orderMandatoryModifiersList = orderMandatoryModifiersList;
    }

    public String getAllMandatoryNames() {
        return getModifierName(orderMandatoryModifiersList);
    }

    public String getAllExcludeNames() {
        return getModifierName(orderExcludeModifiersList);
    }

    public String getAllOptionalNames() {
        return getModifierName(orderOptionalModifiersList);
    }

    public String getModifierName(List<CreatedOrderModifiers> createdOrderModifiersList) {
        StringBuilder resultStr = new StringBuilder();
        if (createdOrderModifiersList.size() > 0) {
            for (int i=0; i < createdOrderModifiersList.size(); i++) {
                CreatedOrderModifiers createdOrderModifiers = createdOrderModifiersList.get(i);
                resultStr.append(i == 0 ? createdOrderModifiers.getModifierItem().getName() : ", " + createdOrderModifiers.getModifierItem().getName());
            }
        }
        return resultStr.toString();
    }

    public double getTotalInDollar() {
        double totalDouble = (double) total;
        return totalDouble / 100;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public CreatedOrderSize getCreatedOrderSize() {
        return createdOrderSize;
    }

    public void setCreatedOrderSize(CreatedOrderSize createdOrderSize) {
        this.createdOrderSize = createdOrderSize;
    }

    public List<CreatedOrderModifiers> getOrderExcludeModifiersList() {
        return orderExcludeModifiersList;
    }

    public void setOrderExcludeModifiersList(List<CreatedOrderModifiers> orderExcludeModifiersList) {
        this.orderExcludeModifiersList = orderExcludeModifiersList;
    }

    public List<CreatedOrderModifiers> getOrderOptionalModifiersList() {
        return orderOptionalModifiersList;
    }

    public void setOrderOptionalModifiersList(List<CreatedOrderModifiers> orderOptionalModifiersList) {
        this.orderOptionalModifiersList = orderOptionalModifiersList;
    }

    public List<CreatedOrderModifiers> getOrderMandatoryModifiersList() {
        return orderMandatoryModifiersList;
    }

    public void setOrderMandatoryModifiersList(List<CreatedOrderModifiers> orderMandatoryModifiersList) {
        this.orderMandatoryModifiersList = orderMandatoryModifiersList;
    }
}
