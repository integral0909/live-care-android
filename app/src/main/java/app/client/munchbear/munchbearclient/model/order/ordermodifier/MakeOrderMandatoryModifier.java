package app.client.munchbear.munchbearclient.model.order.ordermodifier;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */

public class MakeOrderMandatoryModifier {

    @SerializedName("id")
    private int modifiersId;

    public MakeOrderMandatoryModifier(int modifiersId) {
        this.modifiersId = modifiersId;
    }

    public int getModifiersId() {
        return modifiersId;
    }

    public void setModifiersId(int modifiersId) {
        this.modifiersId = modifiersId;
    }

}
