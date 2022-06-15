package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.SliderPromoCode;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.YourReservationActivity;
import app.client.munchbear.munchbearclient.view.adapter.DishAdapter;
import app.client.munchbear.munchbearclient.view.adapter.SliderRVAdapter;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends BaseFragment implements SliderRVAdapter.PromoCodeOnClickListener
        , DishAdapter.ClickDishItem {

    @BindView(R.id.sliderRV) RecyclerView sliderRV;
    @BindView(R.id.dishesRV) RecyclerView disheRV;
    @BindView(R.id.divide1) View divide1;
    @BindView(R.id.signUpToCollectBtn) RelativeLayout signUpToCollectBtn;
    @BindView(R.id.loyaltyBalance) RelativeLayout loyaltyBalance;
    @BindView(R.id.countBalance) TextView countBalance;

    //Temp promo code
    private List<SliderPromoCode> sliderPromoCodeList = new ArrayList<>();

    private MainActivityViewModel viewModel;
    private Unbinder unbinder;
    private SliderRVAdapter sliderRVAdapter;
    private DishAdapter dishAdapter;
    private int loginType;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createTempData();
        loginType = CorePreferencesImpl.getCorePreferences().getLoginType();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

        initObservers();
        initSlider();

        return view;
    }

    private void initObservers() {
        viewModel.getDishCategoryLiveData().observe(this, dishCategoryList -> {
            if (dishCategoryList != null && dishCategoryList.size() > 0) {
                initGuestViews();
                initDishCategoryList(dishCategoryList);
            }
        });

        viewModel.getUserDataLiveData().observe(this, userData -> {
            //countBalance.setText(String.valueOf(userData.getLoyalty_points()));
        });
    }

    private void initSlider() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(sliderRV);

        sliderRVAdapter = new SliderRVAdapter(getContext(), sliderPromoCodeList, this);
        sliderRV.setLayoutManager(layoutManager);
        sliderRV.setItemAnimator(new DefaultItemAnimator());
        sliderRV.setAdapter(sliderRVAdapter);
    }

    private void initDishCategoryList(List<DishCategory> dishCategoryList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        dishAdapter = new DishAdapter(getContext(), dishCategoryList, this);
        disheRV.setLayoutManager(layoutManager);
        disheRV.setItemAnimator(new DefaultItemAnimator());
        disheRV.setAdapter(dishAdapter);
    }

    private void initGuestViews() {
        signUpToCollectBtn.setVisibility(loginType == LoginType.GUEST ? View.VISIBLE : View.GONE);
        LoyaltyProgram.showViewForGuest(loyaltyBalance, loginType == LoginType.GUEST);
        LoyaltyProgram.showProfileHeader(signUpToCollectBtn, loginType == LoginType.GUEST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void createTempData() { //TODO Delete this method when will get data from api
        SliderPromoCode sPromoCode1 = new SliderPromoCode("123ABC", "https://someurl", "Buy 1 /n get 1 FREE");
        SliderPromoCode sPromoCode2 = new SliderPromoCode("123ABC2", "https://someurl", "Buy 1 /n get 1 FREE");
        SliderPromoCode sPromoCode3 = new SliderPromoCode("123ABC3", "https://someurl", "Buy 1 /n get 1 FREE");
        SliderPromoCode sPromoCode4 = new SliderPromoCode("123ABC4", "https://someurl", "Buy 1 /n get 1 FREE");

        sliderPromoCodeList.add(sPromoCode1);
        sliderPromoCodeList.add(sPromoCode2);
        sliderPromoCodeList.add(sPromoCode3);
        sliderPromoCodeList.add(sPromoCode4);
    }

    @Override
    public void onItemPromoCodeClick(int position) {
        viewModel.handleItemPromoCodeClick(position);
    }

    @OnClick(R.id.makeReservationBtn)
    public void clickTableReservationBtn() {
        if (NetworkUtils.isConnectionAvailable(getContext())) {
            if (AvailableRestaurants.getInstance().isAvailableReservationRest()) {
                Intent intent = new Intent(getContext(), YourReservationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.res_not_available_reservation), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.singUp)
    public void clickSingUpBtn() {
        viewModel.logout();
    }

    @Override
    public void dishItemClick(int position) {
        viewModel.openPreMenu();
    }
}
