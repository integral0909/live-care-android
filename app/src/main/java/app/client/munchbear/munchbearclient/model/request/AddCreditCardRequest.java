package app.client.munchbear.munchbearclient.model.request;

import com.google.gson.annotations.SerializedName;

public class AddCreditCardRequest {

    @SerializedName("token")
    private String token;

    @SerializedName("exp_year")
    private String expYear;

    @SerializedName("exp_month")
    private String expMonth;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("brand")
    private String cardBrand;

    public AddCreditCardRequest(String token, String expYear, String expMonth, String cardNumber, String cardBrand) {
        this.token = token;
        this.expYear = expYear;
        this.expMonth = expMonth;
        this.cardNumber = cardNumber;
        this.cardBrand = cardBrand;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expDate) {
        this.expYear = expDate;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }
}
