package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CreditCardClickListener;
import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.view.adapter.CreditCardAdapter;
import app.client.munchbear.munchbearclient.viewmodel.CardListFragmentViewModel;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CardListFragment extends BaseFragment implements CreditCardClickListener {

    @BindView(R.id.cardRV) RecyclerView cardRv;
    @BindView(R.id.addCardBtn) TextView addCardBtn;
    @BindView(R.id.cardsTitle) TextView cardsTitle;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private CardListFragmentViewModel cardListFragmentViewModel;
    private MainActivityViewModel mainActivityViewModel;

    private CreditCardAdapter creditCardAdapter;
    private Unbinder unbinder;

    private List<CreditCard> creditCardList = new ArrayList<>();

    public static CardListFragment newInstance() {
        CardListFragment fragment = new CardListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardListFragmentViewModel = ViewModelProviders.of(this).get(CardListFragmentViewModel.class);
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paymen_method_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initObservers();

        getCardList();

        return view;
    }

    private void initObservers() {
        mainActivityViewModel.getRefreshCardList().observe(getActivity(), refresh -> getCardList());
        cardListFragmentViewModel.getCreditCardListLiveData().observe(this, cardList -> handleCardListResponse(cardList));
        cardListFragmentViewModel.getCreditCardListErrorLiveData().observe(this, error -> Toast.makeText(getContext(), getResources().getString(R.string.toast_card_list_error), Toast.LENGTH_SHORT).show());
        cardListFragmentViewModel.getDeleteCardLiveData().observe(this, deleteSuccess -> {
            if (!deleteSuccess) {
                Toast.makeText(getContext(), getResources().getString(R.string.toast_card_delete_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCardList() {
        progressBar.setVisibility(View.VISIBLE);
        cardListFragmentViewModel.getCardList();
    }

    private void handleCardListResponse(List<CreditCard> creditCards) {
        this.creditCardList = creditCards;
        progressBar.setVisibility(View.GONE);

        cardsTitle.setVisibility(creditCardList.size() > 0 ? View.VISIBLE : View.GONE);
        initCardList();
    }

    private void initCardList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        creditCardAdapter = new CreditCardAdapter(getContext(), creditCardList, this);
        cardRv.setLayoutManager(layoutManager);
        cardRv.setItemAnimator(new DefaultItemAnimator());
        cardRv.setAdapter(creditCardAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.addCardBtn)
    public void clickAddCard() {
        mainActivityViewModel.openAddCardFragment();
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        getActivity().onBackPressed();
    }

    @Override
    public void deleteCard(int cardId) {
        cardListFragmentViewModel.deleteCard(cardId);
    }

    @Override
    public void hideCardListTitle() {
        cardsTitle.setVisibility(View.GONE);
    }

    @Override
    public void selectCard(CreditCard creditCard) {
        //Do nothing
    }

    @Override
    public void editCard(CreditCard creditCard) {
        mainActivityViewModel.openEditCreditCard(creditCard);
    }
}
