package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.payment.CreditCard;

public class CardListResponse {

    @SerializedName("data")
    private List<CreditCard> creditCardList;

    public CardListResponse(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList;
    }

    public List<CreditCard> getCreditCardList() {
        return creditCardList;
    }

    public void setCreditCardList(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList;
    }
}
