package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.DishHelper;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.MunchBearViewPager;
import app.client.munchbear.munchbearclient.utils.TextUtils;
import app.client.munchbear.munchbearclient.utils.ViewUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.CartActivity;
import app.client.munchbear.munchbearclient.view.PreMenuActivity;
import app.client.munchbear.munchbearclient.view.adapter.DishCategoryAdapter;
import app.client.munchbear.munchbearclient.view.adapter.MenuAdapter;
import app.client.munchbear.munchbearclient.view.adapter.MenuViewPagerAdapter;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MenuFragment extends BaseFragment implements DishCategoryAdapter.CategoryItemClickListener,
        MenuAdapter.MenuItemClickListener {

    @BindView(R.id.menuViewPager) MunchBearViewPager viewPager;
    @BindView(R.id.yourCartBtn) ConstraintLayout yourCartBtn;
    @BindView(R.id.dishCategoryRV) RecyclerView dishCategoryRV;
    @BindView(R.id.allDishRV) RecyclerView allDishRV;
    @BindView(R.id.searchBtn) Button searchBtn;
    @BindView(R.id.searchLayout) RelativeLayout searchLayout;
    @BindView(R.id.searchEditText) AppCompatEditText searchEditText;
    @BindView(R.id.cartTotalDollar) TextView cartTotalDollar;
    @BindView(R.id.cartTotalPoint) TextView cartTotalPoint;
    @BindView(R.id.menuTvDeliveryFromTitle) TextView menuTvDeliveryFromTitle;
    @BindView(R.id.menuTvDeliveryFromStreet) TextView restaurantAddressTxt;
    @BindView(R.id.menuTvDeliveryFromPlace) TextView restaurantTitleTxt;
    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.orTxt) TextView orTxt;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private MainActivityViewModel viewModel;
    private Unbinder unbinder;
    private DishCategoryAdapter dishCategoryAdapter;
    private MenuAdapter menuAdapter;
    private Restaurant restaurant;
    private int selectedPosition = 0;

    public static MenuFragment newInstance() {
        MenuFragment menuFragment = new MenuFragment();
        return menuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        initObservers();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        setupViews();
        CorePreferencesImpl.getCorePreferences().setForceOpenMenu(false);

        progressBar.setVisibility(View.VISIBLE);

        initMenu();
        LoyaltyProgram.setupCartViews(orTxt, imageStar, cartTotalPoint, cartTotalDollar);

        return view;
    }

    private void initObservers() {
        viewModel.getShowMenuLiveData().observe(this, position -> showCorrectMenu(position));
        viewModel.getCartChangesLiveData().observe(this, change -> checkCart());
        viewModel.getAllDishListMutableLiveData().observe(this, allDishList -> menuAdapter.refreshAdapter(allDishList));
        viewModel.getDishCategoryErrorLiveData().observe(this, error -> cleanAllMenuData());

        viewModel.getDishCategoryLiveData().observe(this, dishCategoryList -> {
            selectedPosition = DishHelper.getSelectedCategoryIndex(dishCategoryList);
            dishCategoryAdapter.refreshAdapter(dishCategoryList, selectedPosition);
            dishCategoryRV.scrollToPosition(selectedPosition);
        });

        viewModel.getDishCategoryIdsMutableLiveData().observe(this, allIds -> {
            if (AvailableRestaurants.getInstance().isNeedRefresh()) {
                setupMenuViewPager(allIds);
                viewPager.setCurrentItem(selectedPosition, false);
            }
        });

    }

    private void initMenu() {
        if(CorePreferencesImpl.getCorePreferences().getSelectedRestaurantId() != restaurant.getId()) {
            viewModel.initRestaurantMenu(restaurant.getId(), restaurant.isLoyaltyProgramEnable(), false);
        } else {
            viewModel.handleMenuResponse(CorePreferencesImpl.getCorePreferences()
                    .getSelectedRestaurantCategory(), false);
        }
    }

    private void cleanAllMenuData() {
        dishCategoryAdapter.clearData();
        menuAdapter.cleanData();
        progressBar.setVisibility(View.GONE);
        List<DishCategory> dcl = new ArrayList<>();
        CorePreferencesImpl.getCorePreferences().setSelectedRestaurantCategory(dcl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCart();
    }

    public boolean onBackPressed() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            changeSearchViewsState();
            return false;
        } else {
            return true;
        }
    }

    private void setupViews() {
        setupHeaderViews();
        setupSearchView();
        setupDishCategory();
        setupAllDish();
    }

    private void setupHeaderViews() {
        restaurant = Cart.getInstance().getSelectedRestaurant();
        menuTvDeliveryFromTitle.setText(DeliveryVariant.getDeliveryFromType(Cart.getInstance().getSelectedDeliveryType(), getContext()));
        if (restaurant != null) {
            restaurantTitleTxt.setText(restaurant.getName());
            restaurantAddressTxt.setText(String.format(getResources().getString(R.string.cart_restaurant_address_format),
                    restaurant.getAddress().getHouse(), restaurant.getAddress().getStreet()));
        }
    }

    private void setupMenuViewPager(List<Integer> allIds) {
        MenuViewPagerAdapter menuViewPagerAdapter = new MenuViewPagerAdapter(getChildFragmentManager(), allIds);
        viewPager.setAdapter(menuViewPagerAdapter);
        viewPager.setPagingEnabled(false);
        progressBar.setVisibility(View.GONE);
    }

    private void setupSearchView() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                menuAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setupDishCategory() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dishCategoryAdapter = new DishCategoryAdapter(getContext(), this);
        dishCategoryRV.setLayoutManager(layoutManager);
        dishCategoryRV.setItemAnimator(new DefaultItemAnimator());
        dishCategoryRV.setAdapter(dishCategoryAdapter);
    }

    private void setupAllDish() {
        menuAdapter = new MenuAdapter(getContext(), this);
        allDishRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        allDishRV.setAdapter(menuAdapter);
    }

    private void showCorrectMenu(int position) {
        viewPager.setCurrentItem(position, false);
    }

    private void changeSearchViewsState() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            hideKeyboardFrom(getContext(), getView());
        } else {
            showKeyboard(getContext());
        }
        searchEditText.setText("");
        allDishRV.setVisibility(allDishRV.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        viewPager.setVisibility(viewPager.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        searchLayout.setVisibility(searchLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        dishCategoryRV.setVisibility(dishCategoryRV.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        searchBtn.setBackground(searchLayout.getVisibility() == View.VISIBLE ?
                getResources().getDrawable(R.drawable.menu_search_enable) :
                getResources().getDrawable(R.drawable.menu_search_disable));
    }

    private void checkCart() {
        yourCartBtn.setVisibility(Cart.getInstance().isCartEmpty() ? View.GONE : View.VISIBLE);
        if (!Cart.getInstance().isCartEmpty()) {
            TextUtils.setupTotalPrice(getContext(), cartTotalDollar, cartTotalPoint, Cart.getInstance().getTotalPriceDollar(),
                    Cart.getInstance().getTotalPricePoint(), 1);
        } else {
            viewModel.clearCartView();
        }
    }

    @Override
    public void categoryItemClick(int position) {
        viewModel.handleBtnClick(position);
    }

    @Override
    public void onClick(Dish dish) {
        hideKeyboardFrom(getContext(), getView());
        viewModel.handleSubMenuItemClick(dish);
    }

    @OnClick(R.id.yourCartBtn)
    public void clickYourCart() {
        openActivity(getContext(), CartActivity.class);
    }

    @OnClick(R.id.searchBtn)
    public void clickSearchBtn() {
        changeSearchViewsState();
    }

    @OnClick(R.id.cancelBtnTxt)
    public void clickCancelBtnTxt(View view) {
        changeSearchViewsState();
        viewModel.onMenuSearchCancelBtnClick();
        hideKeyboardFrom(getContext(), view);
    }

    @OnClick(R.id.menuDeliveryFromContainer)
    public void clickChangeDeliveryType() {
        AvailableRestaurants.getInstance().setNeedRefresh(false);
        startActivity(new Intent(getContext(), PreMenuActivity.class));
    }

    @OnClick(R.id.cleanSearch)
    public void  clickCleanSearch() {
        searchEditText.setText("");
        menuAdapter.filter("");
    }
}
