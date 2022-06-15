package app.client.munchbear.munchbearclient.model.payment;

import com.google.gson.annotations.SerializedName;

import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_DELIVERY;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_EAT_IN;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_PICK_UP;

public class PaymentMethod {

    @SerializedName("pick_up_card")
    boolean pickUpCard;

    @SerializedName("pick_up_cash")
    boolean pickUpCash;

    @SerializedName("pick_up_loyalty_points")
    boolean pickUpLoyalty;

    @SerializedName("delivery_card")
    boolean deliveryCard;

    @SerializedName("delivery_cash")
    boolean deliveryCash;

    @SerializedName("delivery_loyalty_points")
    boolean deliveryLoyalty;

    @SerializedName("eat_in_card")
    boolean eatInCard;

    @SerializedName("eat_in_cash")
    boolean eatInCash;

    @SerializedName("eat_in_loyalty_points")
    boolean eatInLoyalty;

    public PaymentMethod(boolean pickUpCard, boolean pickUpCash, boolean pickUpLoyalty, boolean deliveryCard,
                         boolean deliveryCash, boolean deliveryLoyalty, boolean eatInCard, boolean eatInCash, boolean eatInLoyalty) {
        this.pickUpCard = pickUpCard;
        this.pickUpCash = pickUpCash;
        this.pickUpLoyalty = pickUpLoyalty;
        this.deliveryCard = deliveryCard;
        this.deliveryCash = deliveryCash;
        this.deliveryLoyalty = deliveryLoyalty;
        this.eatInCard = eatInCard;
        this.eatInCash = eatInCash;
        this.eatInLoyalty = eatInLoyalty;
    }

    public boolean isCardEnable(int selectedDeliveryType) {
        switch (selectedDeliveryType) {
            case TYPE_DELIVERY: return isDeliveryCard();
            case TYPE_EAT_IN: return isEatInCard();
            case TYPE_PICK_UP: return isPickUpCard();
            default: return false;
        }
    }

    public boolean isCashEnable(int selectedDeliveryType) {
        switch (selectedDeliveryType) {
            case TYPE_DELIVERY: return isDeliveryCash();
            case TYPE_EAT_IN: return isEatInCash();
            case TYPE_PICK_UP: return isPickUpCash();
            default: return false;
        }
    }

    public boolean isLoyaltyEnable(int selectedDeliveryType) {
        switch (selectedDeliveryType) {
            case TYPE_DELIVERY: return isDeliveryLoyalty();
            case TYPE_EAT_IN: return isEatInLoyalty();
            case TYPE_PICK_UP: return isPickUpLoyalty();
            default: return false;
        }
    }

    public boolean isPickUpCard() {
        return pickUpCard;
    }

    public void setPickUpCard(boolean pickUpCard) {
        this.pickUpCard = pickUpCard;
    }

    public boolean isPickUpCash() {
        return pickUpCash;
    }

    public void setPickUpCash(boolean pickUpCash) {
        this.pickUpCash = pickUpCash;
    }

    public boolean isPickUpLoyalty() {
        return pickUpLoyalty;
    }

    public void setPickUpLoyalty(boolean pickUpLoyalty) {
        this.pickUpLoyalty = pickUpLoyalty;
    }

    public boolean isDeliveryCard() {
        return deliveryCard;
    }

    public void setDeliveryCard(boolean deliveryCard) {
        this.deliveryCard = deliveryCard;
    }

    public boolean isDeliveryCash() {
        return deliveryCash;
    }

    public void setDeliveryCash(boolean deliveryCash) {
        this.deliveryCash = deliveryCash;
    }

    public boolean isDeliveryLoyalty() {
        return deliveryLoyalty;
    }

    public void setDeliveryLoyalty(boolean deliveryLoyalty) {
        this.deliveryLoyalty = deliveryLoyalty;
    }

    public boolean isEatInCard() {
        return eatInCard;
    }

    public void setEatInCard(boolean eatInCard) {
        this.eatInCard = eatInCard;
    }

    public boolean isEatInCash() {
        return eatInCash;
    }

    public void setEatInCash(boolean eatInCash) {
        this.eatInCash = eatInCash;
    }

    public boolean isEatInLoyalty() {
        return eatInLoyalty;
    }

    public void setEatInLoyalty(boolean eatInLoyalty) {
        this.eatInLoyalty = eatInLoyalty;
    }
}
