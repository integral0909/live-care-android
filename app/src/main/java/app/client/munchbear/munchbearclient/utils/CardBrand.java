package app.client.munchbear.munchbearclient.utils;

import com.braintreepayments.cardform.utils.CardType;

public enum CardBrand {

    UNKNOWN("Unknown", CardType.UNKNOWN),
    VISA("Visa", CardType.VISA),
    MASTERCARD("MasterCard", CardType.MASTERCARD),
    AMEX("American Express", CardType.AMEX),
    DISCOVER("Discover", CardType.DISCOVER),
    DINERS_CLUB("Diners Club", CardType.DINERS_CLUB),
    JCB("JCB", CardType.JCB),
    UNIONPAY("UnionPay", CardType.UNIONPAY);

    private String cardBrand;
    private CardType cardType;

    CardBrand(String cardBrand, CardType cardType) {
        this.cardBrand = cardBrand;
        this.cardType = cardType;
    }

    public static String getCardBrendName(CardType cardType) {
        for (CardBrand cardBrand : CardBrand.values()) {
            if (cardBrand.getCardType() == cardType) {
                return cardBrand.getBrandName();
            }
        }

        return UNKNOWN.getBrandName();
    }

    public static CardType getCartTypeByName(String name) {
        for (CardBrand cardBrand : CardBrand.values()) {
            if (cardBrand.getBrandName().equals(name)) {
                return cardBrand.getCardType();
            }
        }

        return CardType.UNKNOWN;
    }

    public String getBrandName() {
        return cardBrand;
    }

    public void setBrandName(String cardName) {
        this.cardBrand = cardName;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
