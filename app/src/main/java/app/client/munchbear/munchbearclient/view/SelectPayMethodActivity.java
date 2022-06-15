package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CreditCardClickListener;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.payment.PaymentMethod;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.view.adapter.CreditCardAdapter;
import app.client.munchbear.munchbearclient.view.fragment.AddCardFragment;
import app.client.munchbear.munchbearclient.viewmodel.SelectPayMethodActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPayMethodActivity extends BaseActivity implements CreditCardClickListener {

    @BindView(R.id.loyaltyBalance) TextView loyaltyBalance;
    @BindView(R.id.loyaltyMethodIcon) ImageView loyaltyMethodIcon;
    @BindView(R.id.cashLayout) RelativeLayout cashLayout;
    @BindView(R.id.loyaltyLayout) RelativeLayout loyaltyLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.cardsTitle) TextView cardsTitle;
    @BindView(R.id.cardRV) RecyclerView cardRV;
    @BindView(R.id.nestedScrollViewCards) NestedScrollView nestedScrollViewCards;
    @BindView(R.id.container) FrameLayout container;

    public static final int SELECT_PAYMENT_REQUEST_CODE = 104;
    public static final String KEY_PAYMENT_TYPE = "key.payment.type";
    public static final String KEY_CARD_ID = "key.card.id";
    public static final String KEY_CARD_LAST_NUMBERS = "key.card.last.number";

    public static final String PAYMENT_METHOD_CASH = "cash";
    public static final String PAYMENT_METHOD_LOYALTY = "loyalty_points";
    public static final String PAYMENT_METHOD_CARD = "card";

    private String paymentType = "";
    private SelectPayMethodActivityViewModel selectPayMethodActivityViewModel;
    private CreditCardAdapter creditCardAdapter;
    private List<CreditCard> creditCardList = new ArrayList<>();
    private UserData userData;
    private CreditCard selectedCreditCard;
    private int deliveryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pay_method);
        ButterKnife.bind(this);

        deliveryType = Cart.getInstance().getSelectedDeliveryType();

        selectPayMethodActivityViewModel = ViewModelProviders.of(this).get(SelectPayMethodActivityViewModel.class);
        initObservers();

        progressBar.setVisibility(View.VISIBLE);
        selectPayMethodActivityViewModel.getPaymentMethod();
        selectPayMethodActivityViewModel.getUserData();
        selectPayMethodActivityViewModel.getCardList();
    }

    private void initObservers() {
        selectPayMethodActivityViewModel.getUserDataLiveData().observe(this, userData -> handleUserData(userData));
        selectPayMethodActivityViewModel.getUserDataErrorLiveData().observe(this, error -> Toast.makeText(this, getResources().getString(R.string.toast_get_loyalty_point_error), Toast.LENGTH_SHORT).show());
        selectPayMethodActivityViewModel.getPaymentMethodLiveData().observe(this, paymentMethod -> handlePaymentMethod(paymentMethod));
        selectPayMethodActivityViewModel.getPaymentMethodErrorLiveData().observe(this, error -> Toast.makeText(this, getResources().getString(R.string.toast_get_payment_method_error), Toast.LENGTH_SHORT).show());

        selectPayMethodActivityViewModel.getCreditCardListLiveData().observe(this, cardList -> handleCardListResponse(cardList));
        selectPayMethodActivityViewModel.getCreditCardListErrorLiveData().observe(this, error -> Toast.makeText(this, getResources().getString(R.string.toast_card_list_error), Toast.LENGTH_SHORT).show());
        selectPayMethodActivityViewModel.getDeleteCardLiveData().observe(this, deleteSuccess -> {
            if (!deleteSuccess) {
                Toast.makeText(this, getResources().getString(R.string.toast_card_delete_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserData(UserData userData) {
        this.userData = userData;
        setupLoyaltyBalance();
    }

    private void handleCardListResponse(List<CreditCard> creditCards) {
        this.creditCardList = creditCards;
        progressBar.setVisibility(View.GONE);

        cardsTitle.setVisibility(creditCardList.size() > 0 ? View.VISIBLE : View.GONE);
        initCardList();
    }

    private void initCardList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        creditCardAdapter = new CreditCardAdapter(this, creditCardList, this);
        creditCardAdapter.disablePopUp();
        cardRV.setLayoutManager(layoutManager);
        cardRV.setItemAnimator(new DefaultItemAnimator());
        cardRV.setAdapter(creditCardAdapter);
    }

    private void setupLoyaltyBalance() {
//        String userPointBalance = String.valueOf(userData.getLoyalty_points());
//
//        if (!isEnoughPoints()) {
//            loyaltyMethodIcon.setImageResource(R.mipmap.star_grey);
//            loyaltyBalance.setTextColor(getResources().getColor(R.color.greyLoyalty));
//            loyaltyBalance.setText(String.format(getResources().getString(R.string.pay_method_low_points), userPointBalance));
//        } else {
//            loyaltyBalance.setText(String.format(getResources().getString(R.string.pay_method_you_have), userPointBalance));
//        }
    }

    private boolean isEnoughPoints() {
//        return userData.getLoyalty_points() >= Cart.getInstance().getTotalPricePoint();
        return true;
    }

    private void handlePaymentMethod(PaymentMethod paymentMethod) {
        progressBar.setVisibility(View.GONE);
        nestedScrollViewCards.setVisibility(paymentMethod.isCardEnable(deliveryType) ? View.VISIBLE : View.GONE);
        cashLayout.setVisibility(paymentMethod.isCashEnable(deliveryType) ? View.VISIBLE : View.GONE);
        loyaltyLayout.setVisibility(paymentMethod.isLoyaltyEnable(deliveryType) ? View.VISIBLE : View.GONE);
        loyaltyBalance.setVisibility(paymentMethod.isLoyaltyEnable(deliveryType) ? View.VISIBLE : View.GONE);
    }

    private void finishWithResult() {
        if (paymentType != null && !paymentType.isEmpty()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_PAYMENT_TYPE, paymentType);
            if (paymentType.equals(PAYMENT_METHOD_CARD)) {
                resultIntent.putExtra(KEY_CARD_ID, selectedCreditCard.getId());
                resultIntent.putExtra(KEY_CARD_LAST_NUMBERS, selectedCreditCard.getLast_four());
            }
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        super.onBackPressed();
    }

    @OnClick(R.id.cashLayout)
    public void clickCash() {
        paymentType = PAYMENT_METHOD_CASH;
        finishWithResult();
    }

    @OnClick(R.id.loyaltyLayout)
    public void clickLoyalty() {
        if (isEnoughPoints()) {
            paymentType = PAYMENT_METHOD_LOYALTY;
            finishWithResult();
        }
    }

    @OnClick(R.id.addCardBtn)
    public void clickAddCard() {
        replaceFragment(AddCardFragment.newInstance(false), true, R.id.container, true, false);
    }

    @Override
    public void deleteCard(int cardId) {//Do nothing
    }

    @Override
    public void editCard(CreditCard creditCard) { //Do nothing
    }

    @Override
    public void hideCardListTitle() {
//        cardsTitle.setVisibility(View.GONE);
    }

    @Override
    public void selectCard(CreditCard creditCard) {
        selectedCreditCard = creditCard;
        paymentType = PAYMENT_METHOD_CARD;
        finishWithResult();
    }
}
