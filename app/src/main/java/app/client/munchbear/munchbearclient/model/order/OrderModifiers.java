package app.client.munchbear.munchbearclient.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderExcludeModifier;
import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderMandatoryModifier;
import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderOptionalModifier;

/**
 * @author Roman H.
 */

public class OrderModifiers {

    @SerializedName("exclude")
    private List<MakeOrderExcludeModifier> excludeModifiers;

    @SerializedName("optional")
    private List<MakeOrderOptionalModifier> optionalModifiers;

    @SerializedName("mandatory")
    private List<MakeOrderMandatoryModifier> mandatoryModifiers;

    public OrderModifiers(List<MakeOrderExcludeModifier> excludeModifiers, List<MakeOrderOptionalModifier> optionalModifiers, List<MakeOrderMandatoryModifier> mandatoryModifiers) {
        this.excludeModifiers = excludeModifiers;
        this.optionalModifiers = optionalModifiers;
        this.mandatoryModifiers = mandatoryModifiers;
    }

    public List<MakeOrderExcludeModifier> getExcludeModifiers() {
        return excludeModifiers;
    }

    public void setExcludeModifiers(List<MakeOrderExcludeModifier> excludeModifiers) {
        this.excludeModifiers = excludeModifiers;
    }

    public List<MakeOrderOptionalModifier> getOptionalModifiers() {
        return optionalModifiers;
    }

    public void setOptionalModifiers(List<MakeOrderOptionalModifier> optionalModifiers) {
        this.optionalModifiers = optionalModifiers;
    }

    public List<MakeOrderMandatoryModifier> getMandatoryModifiers() {
        return mandatoryModifiers;
    }

    public void setMandatoryModifiers(List<MakeOrderMandatoryModifier> mandatoryModifiers) {
        this.mandatoryModifiers = mandatoryModifiers;
    }
}
