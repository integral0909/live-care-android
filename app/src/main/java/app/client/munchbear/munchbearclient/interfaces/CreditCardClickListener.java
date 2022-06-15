package app.client.munchbear.munchbearclient.interfaces;

import app.client.munchbear.munchbearclient.model.payment.CreditCard;

public interface CreditCardClickListener {

    void deleteCard(int cardId);
    void editCard(CreditCard creditCard);
    void hideCardListTitle();
    void selectCard(CreditCard creditCard);

}
