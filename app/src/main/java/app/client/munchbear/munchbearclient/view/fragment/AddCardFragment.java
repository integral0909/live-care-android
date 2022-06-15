package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;

import net.authorize.acceptsdk.AcceptSDKApiClient;
import net.authorize.acceptsdk.datamodel.common.Message;
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication;
import net.authorize.acceptsdk.datamodel.transaction.CardData;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionType;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.request.AddCreditCardRequest;
import app.client.munchbear.munchbearclient.utils.CardBrand;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.viewmodel.AddCardFragmentViewModel;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import app.client.munchbear.munchbearclient.viewmodel.SelectPayMethodActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddCardFragment extends BaseFragment implements EncryptTransactionCallback, CardEditText.OnCardTypeChangedListener {

    @BindView(R.id.cardForm) CardForm cardForm;
    @BindView(R.id.progressBarLayout) RelativeLayout progressBarLayout;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;

    private static final String KEY_EDITING_CARD = "key.editing.card";
    private static final String KEY_CREDIT_CARD = "key.credit.card";
    private static final String KEY_FOR_PROFILE_CARDS = "key.credit.for.profile";

    private static final String AUTHORIZE_API_LOGIN_ID = "8uT3mWp79xfv";
    private static final String AUTHORIZE_CLIENT_KEY = "4sCHR3FEt3C2VZP4v73sTur9s3k5szzmyBeRqjxvrge7uW2UR9P263R4VaWGeFUA";
    public static final int CONNECT_TIMEOUT = 5000;

    private AddCardFragmentViewModel addCardFragmentViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private SelectPayMethodActivityViewModel selectPayMethodActivityViewModel;
    private Unbinder unbinder;
    private AcceptSDKApiClient apiClient;
    private String cardBrand;
    private boolean editingCard = false;
    private CreditCard creditCard;
    private boolean forProfileCards;

    public static AddCardFragment newInstance(boolean forProfileCards) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_FOR_PROFILE_CARDS, forProfileCards);
        fragment.setArguments(bundle);
        return new AddCardFragment();
    }

    public static AddCardFragment newInstance(CreditCard creditCard, boolean forProfileCards) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_EDITING_CARD, true);
        bundle.putParcelable(KEY_CREDIT_CARD, creditCard);
        bundle.putBoolean(KEY_FOR_PROFILE_CARDS, forProfileCards);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFromArguments();

        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        addCardFragmentViewModel = ViewModelProviders.of(this).get(AddCardFragmentViewModel.class);
        selectPayMethodActivityViewModel = ViewModelProviders.of(getActivity()).get(SelectPayMethodActivityViewModel.class);

        setupApiClient();
    }

    private void getDataFromArguments() {
        if (getArguments() != null) {
            forProfileCards = getArguments().getBoolean(KEY_FOR_PROFILE_CARDS);
            if (getArguments().containsKey(KEY_EDITING_CARD) && getArguments().containsKey(KEY_CREDIT_CARD)) {
                editingCard = getArguments().getBoolean(KEY_EDITING_CARD);
                creditCard = getArguments().getParcelable(KEY_CREDIT_CARD);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_card_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initViews();
        initObservers();

        return view;
    }

    private void initObservers() {
        addCardFragmentViewModel.getAddCreditCardLiveData().observe(this, addedCard -> finishWithResult());
        addCardFragmentViewModel.getAddCreditCardErrorLiveData().observe(this, error -> Toast.makeText(getContext(), getResources().getString(error ? R.string.toast_add_card_error : R.string.toast_add_card_duplicate), Toast.LENGTH_SHORT).show());
        addCardFragmentViewModel.getIsLoading().observe(this, isLoading -> progressBarLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        addCardFragmentViewModel.getUpdateCreditCardLiveData().observe(this, updated -> handleUpdateCard(updated));
    }

    private void initViews() {
        toolbarTitle.setText(editingCard ? getResources().getString(R.string.add_card_edit_toolbar_title) : getResources().getString(R.string.add_card_toolbar_title));
            setupCardForm();

        if (editingCard) {
            cardForm.getExpirationDateEditText().setText(String.format("%02d%d", creditCard.getExpMonth(), creditCard.getExpYear()));
        }
    }

    private void setupApiClient() {
        apiClient = new AcceptSDKApiClient.Builder(getActivity(), AcceptSDKApiClient.Environment.SANDBOX)
                .connectionTimeout(CONNECT_TIMEOUT)
                .build();
    }

    private void setupCardForm() {
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(getActivity());
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        cardForm.setOnCardTypeChangedListener(this);

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.bottomMenuTextGrey));
        cardForm.getCardEditText().setBackgroundTintList(colorStateList);

    }

    private void getAuthorizeToken() {
        EncryptTransactionObject transactionObject = TransactionObject.
                createTransactionObject(TransactionType.SDK_TRANSACTION_ENCRYPTION)
                .cardData(getCardData())
                .merchantAuthentication(getMerchantAuthentication())
                .build();

        apiClient.getTokenWithRequest(transactionObject, this);
    }

    private CardData getCardData() {
        CardData cardData = new CardData.Builder(cardForm.getCardNumber(),
                cardForm.getExpirationMonth(),
                cardForm.getExpirationYear())
                .cvvCode(cardForm.getCvv())
                .build();

        return cardData;
    }

    private ClientKeyBasedMerchantAuthentication getMerchantAuthentication() {
        return ClientKeyBasedMerchantAuthentication.createMerchantAuthentication(AUTHORIZE_API_LOGIN_ID, AUTHORIZE_CLIENT_KEY);
    }

    private void finishWithResult() {
        if (forProfileCards) {
            mainActivityViewModel.refreshCardList();
        } else {
            selectPayMethodActivityViewModel.refreshCardList();
        }
        getActivity().onBackPressed();
    }

    private void handleUpdateCard(boolean updated) {
        if (updated) {
            finishWithResult();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.toast_update_card_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.rootLayout)
    public void clickRoot(View view) {
        hideKeyboardFrom(getContext(), view);
    }

    @OnClick(R.id.addBtn)
    public void clickAddCard() {
        if (NetworkUtils.isConnectionAvailable(getContext()) && progressBarLayout.getVisibility() == View.GONE) {
            if (cardForm.isValid()) {
//            showConfirmAlert();
                addCardFragmentViewModel.showLoading(true);
                getAuthorizeToken();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.add_card_complete_form), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        getActivity().onBackPressed();
    }

    @Override
    public void onErrorReceived(ErrorTransactionResponse errorResponse) {
        addCardFragmentViewModel.showLoading(false);
        Message error = errorResponse.getFirstErrorMessage();
        Toast.makeText(getActivity(),
                error.getMessageCode() + " : " + error.getMessageText() ,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response) {
        AddCreditCardRequest creditCardRequest = new AddCreditCardRequest(response.getDataValue(),
                cardForm.getExpirationYear(),
                cardForm.getExpirationMonth(),
                cardForm.getCardNumber(),
                cardBrand);

        if (editingCard) {
            addCardFragmentViewModel.updateCard(creditCardRequest, creditCard.getId());
        } else {
            addCardFragmentViewModel.addCard(creditCardRequest);
        }
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType != CardType.EMPTY) {
            cardBrand = CardBrand.getCardBrendName(cardType);
        }
    }
}
