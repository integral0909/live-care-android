package app.client.munchbear.munchbearclient.model.order.ordermodifier;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class MakeOrderOptionalModifier {

    @SerializedName("id")
    private int modifiersId;

    @SerializedName("quantity")
    private int quantity;

    public MakeOrderOptionalModifier(int modifiersId, int quantity) {
        this.modifiersId = modifiersId;
        this.quantity = quantity;
    }

    public int getModifiersId() {
        return modifiersId;
    }

    public void setModifiersId(int modifiersId) {
        this.modifiersId = modifiersId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
