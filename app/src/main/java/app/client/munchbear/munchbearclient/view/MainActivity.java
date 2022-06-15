package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishEventsListener;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.reservation.Reservation;
import app.client.munchbear.munchbearclient.model.response.MyOrdersResponse;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.fragment.AddCardFragment;
import app.client.munchbear.munchbearclient.view.fragment.CardListFragment;
import app.client.munchbear.munchbearclient.view.fragment.DeliveryAddressFragment;
import app.client.munchbear.munchbearclient.view.fragment.DetailDishSelectionFragment;
import app.client.munchbear.munchbearclient.view.fragment.MainFragment;
import app.client.munchbear.munchbearclient.view.fragment.MenuFragment;
import app.client.munchbear.munchbearclient.view.fragment.MyOrdersFragment;
import app.client.munchbear.munchbearclient.view.fragment.ProfileFragment;
import app.client.munchbear.munchbearclient.view.fragment.PromoCodeFragment;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.client.munchbear.munchbearclient.view.fragment.MyOrderListFragment.FIRST_PAGIN_POSTFIX;

public class MainActivity extends BaseActivity implements DishEventsListener {

    @BindView(R.id.navigation)
    AHBottomNavigation navigationMenu;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.transparentBackground)
    FrameLayout transparentBackground;

    public static final int PRE_MENU_ACTIVITY_REQUEST_CODE = 313;

    private MainActivityViewModel mainActivityViewModel;

    private final int MAIN_ITEM_POSITION = 0;
    private final int MENU_ITEM_POSITION = 1;
    private final int MY_ORDERS_ITEM_POSITION = 2;
    private final int PROFILE_ITEM_POSITION = 3;

    private int previouslySelectedTabPosition = 0;
    private Resources resources;
    private PromoCodeFragment promoCodeFragment;
    private DeliveryAddressFragment deliveryAddressFragment;
    private CardListFragment cardListFragment;

    private BroadcastReceiver updateUIReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.registerFCMToken();
        setupReceiver();
        getUserData();
        getRestaurantList();

        mainActivityViewModel.getMyOrdersList(FIRST_PAGIN_POSTFIX);
        resources = getResources();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        deliveryAddressFragment = DeliveryAddressFragment.newInstance(true);
        cardListFragment = CardListFragment.newInstance();

        initObservers();

        initBottomNavigationView();
        replaceFragment(MainFragment.newInstance(), false, R.id.container, false, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeNavMenuItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRE_MENU_ACTIVITY_REQUEST_CODE) {
            handlePreMenuResult(resultCode == RESULT_OK);
        }
    }

    @Override
    public void onDishAdded(Dish dish) {
        mainActivityViewModel.addDishToCart(dish);
        onBackPressed();
    }

    @Override
    public void onDishEdited(Dish dish, int position) {

    }

    private void changeNavMenuItem() {
        if (CorePreferencesImpl.getCorePreferences().isNeedForceOpenMenu()) {
            navigationMenu.setCurrentItem(MENU_ITEM_POSITION);
        } else if (CorePreferencesImpl.getCorePreferences().isNeedForceOpenMyOrder()) {
            navigationMenu.setCurrentItem(MY_ORDERS_ITEM_POSITION);
            CorePreferencesImpl.getCorePreferences().setForceOpenMyOrder(false);
        }
    }

    private void setupReceiver() {
        updateUIReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mainActivityViewModel.handleRefreshingBroadcast(intent);
            }
        };

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Reservation.ACTION_RESERVATION_UPDATED);
        localBroadcastManager.registerReceiver(updateUIReceiver, intentFilter);
    }

    private void initObservers() {
        mainActivityViewModel.getOpenDeliveryAddressLiveData().observe(this, openDeliveryAddress ->
                replaceFragment(DeliveryAddressFragment.newInstance(true), true, R.id.container, true, false));

        mainActivityViewModel.getOpenPaymentMethodLiveData().observe(this, aBoolean ->
                replaceFragment(CardListFragment.newInstance(), true, R.id.container, true, false));

        mainActivityViewModel.getShowPromoCodeFragmentLiveData().observe(this, position -> replacePromoCodeFragment());

        mainActivityViewModel.getShowSubMenuDetailsLiveData().observe(this, dish -> {
            navigationMenu.hideBottomNavigation(true);
            transparentBackground.setVisibility(View.VISIBLE);
            replaceFragment(DetailDishSelectionFragment.newInstance(dish, false, 0), true, R.id.promoCodeContainer, true, true);
            Log.i("On submenu item click", "");
        });

        mainActivityViewModel.getLogoutLiveData().observe(this, isLogout -> {
            if (isLogout) {
                openActivityWithFinish(LoginStarterActivity.class, true);
            }
        });

        mainActivityViewModel.getShowPreMenuLiveData().observe(this, show -> navigationMenu.setCurrentItem(MENU_ITEM_POSITION));

        mainActivityViewModel.getRefreshReservationsLiveData().observe(this, refresh -> {
            if (true) {
                mainActivityViewModel.getMyReservationList("");
            } else {
                //TODO Get order list
            }
        });

        mainActivityViewModel.getMyOrdersResponseLiveData().observe(this, myOrdersResponse -> setBadgeCount(myOrdersResponse));
        mainActivityViewModel.getOpenAddCardFragment().observe(this, openFragment -> replaceFragment(AddCardFragment.newInstance(true), true, R.id.container, true, false));
        mainActivityViewModel.getOpenEditCreditCard().observe(this, creditCard -> replaceFragment(AddCardFragment.newInstance(creditCard, true), true, R.id.container, true, false));
    }

    private void initBottomNavigationView() {

        createAHItem(R.string.title_main, R.drawable.bottom_main_check);
        createAHItem(R.string.title_menu, R.drawable.bottom_menu_check);
        createAHItem(R.string.title_my_orders, R.drawable.ic_my_orders);
        createAHItem(R.string.title_profile, R.drawable.bottom_profile_check);

        Typeface font = ResourcesCompat.getFont(this, R.font.avenir_next_medium);
        navigationMenu.setTitleTypeface(font);

        Typeface fontBadge = ResourcesCompat.getFont(this, R.font.avenir_next_demi_bold);
        navigationMenu.setNotificationTypeface(fontBadge);

        navigationMenu.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        navigationMenu.setTitleTextSizeInSp(10, 10);

        navigationMenu.setAccentColor(Color.parseColor("#77DC00"));
        navigationMenu.setInactiveColor(Color.parseColor("#A9AAAE"));

        navigationMenu.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case MAIN_ITEM_POSITION:
                    replaceFragment(MainFragment.newInstance(), false, R.id.container, false, false);
                    previouslySelectedTabPosition = position;
                    break;
                case MENU_ITEM_POSITION:
                    if (Cart.getInstance().getSelectedDeliveryType() != 0 && Cart.getInstance().getSelectedRestaurant() != null) {
                        replaceFragment(MenuFragment.newInstance(), false, R.id.container, false, false);
                        previouslySelectedTabPosition = MENU_ITEM_POSITION;
                    } else {
                        if (CorePreferencesImpl.getCorePreferences().isNeedForceOpenMenu()) {
                            handlePreMenuResult(true);
                        } else {
                            startActivityForResult(new Intent(this, PreMenuActivity.class), PRE_MENU_ACTIVITY_REQUEST_CODE);
                        }
                    }
                    break;
                case MY_ORDERS_ITEM_POSITION:
                    if (previouslySelectedTabPosition != MY_ORDERS_ITEM_POSITION) {
                        replaceFragment(MyOrdersFragment.newInstance(), false, R.id.container, false, false);
                        navigationMenu.setNotification("", position);
                        previouslySelectedTabPosition = position;
                    }
                    break;
                case PROFILE_ITEM_POSITION:
                    if (getFragmentByTag(deliveryAddressFragment.getClass().getName()) instanceof DeliveryAddressFragment
                            || getFragmentByTag(cardListFragment.getClass().getName()) instanceof CardListFragment) {
                        onBackPressed();
                    } else {
                        replaceFragment(ProfileFragment.newInstance(), false, R.id.container, false, false);
                    }
                    previouslySelectedTabPosition = position;
                    break;
            }

            return true;
        });
    }

    private void handlePreMenuResult(boolean isSuccess) {
        if (isSuccess) {
            replaceFragment(MenuFragment.newInstance(), false, R.id.container, true, false);
            previouslySelectedTabPosition = MENU_ITEM_POSITION;
            CorePreferencesImpl.getCorePreferences().setForceOpenMenu(false);
        } else {
            navigationMenu.setCurrentItem(previouslySelectedTabPosition, false);
        }
    }

    private void createAHItem(int titleId, int drawableId) {
        AHBottomNavigationItem item = new AHBottomNavigationItem(
                resources.getString(titleId),
                resources.getDrawable(drawableId));

        navigationMenu.addItem(item);
    }

    private void createBadge(int itemPosition, String badgeContent) {
        AHNotification badge = new AHNotification.Builder()
                .setText(badgeContent)
                .setBackgroundColor(resources.getColor(R.color.badgeRed))
                .setTextColor(resources.getColor(R.color.colorWhite))
                .build();
        navigationMenu.setNotification(badge, itemPosition);
    }

    public void replacePromoCodeFragment() {
        promoCodeFragment = PromoCodeFragment.newInstance();
        navigationMenu.hideBottomNavigation(true);
        transparentBackground.setVisibility(View.VISIBLE);

        replaceFragment(PromoCodeFragment.newInstance(), true, R.id.promoCodeContainer, true, true);
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment fragment = getFragmentByTag(MenuFragment.class.getName());
        if (fragment instanceof MenuFragment) {
            if (fragment.getFragmentManager().getBackStackEntryCount() > 0) {
                handleBackPressedInActivity();
            } else {
                handleBackPressedInMenu(((MenuFragment) fragment));
            }
        } else {
            handleBackPressedInActivity();
        }
    }

    private void handleBackPressedInMenu(MenuFragment fragment) {
        boolean isNeedBackPress = fragment.onBackPressed();
        if (isNeedBackPress) {
            super.onBackPressed();
        }
    }

    private void handleBackPressedInActivity() {
        if (transparentBackground.getVisibility() == View.VISIBLE) {
            transparentBackground.setVisibility(View.GONE);
            navigationMenu.restoreBottomNavigation(true);
        }

        super.onBackPressed();
    }

    private void getRestaurantList() {
        if (NetworkUtils.isConnectionAvailable(this)) {
            mainActivityViewModel.getRestaurantInsideList();
            mainActivityViewModel.getRestaurantDeliveryList();
        }
    }

    private void getUserData() {
        if (NetworkUtils.isConnectionAvailable(this)) {
            mainActivityViewModel.getUserData();
        }
    }

    private void setBadgeCount(MyOrdersResponse myOrdersResponse) {
        if (myOrdersResponse != null) {
            int ordersCount = myOrdersResponse.getMeta().getTotal(); //TODO
            createBadge(MY_ORDERS_ITEM_POSITION, String.valueOf(ordersCount));
        }
    }
}
