package app.client.munchbear.munchbearclient.model.order.ordermodifier;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class MakeOrderExcludeModifier {

    @SerializedName("id")
    private int modifiersId;

    public MakeOrderExcludeModifier(int modifiersId) {
        this.modifiersId = modifiersId;
    }

    public int getModifiersId() {
        return modifiersId;
    }

    public void setModifiersId(int modifiersId) {
        this.modifiersId = modifiersId;
    }
}
