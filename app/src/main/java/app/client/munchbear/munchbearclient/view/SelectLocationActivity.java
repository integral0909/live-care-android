package app.client.munchbear.munchbearclient.view;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.restaurant.DeliveryZone;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.adapter.SelectLocationAdapter;
import app.client.munchbear.munchbearclient.viewmodel.SelectLocationActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.view.fragment.BaseFragment.INTENT_BUNDLE;
import static app.client.munchbear.munchbearclient.view.fragment.PreMenuFragment.MENU_OPEN_TYPE;

public class SelectLocationActivity extends UserLocationBaseActivity
        implements OnMapReadyCallback, SelectLocationAdapter.OnRestaurantClick {

    @BindView(R.id.slidingPaneLayout) SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.locationRV) RecyclerView locationRV;
    @BindView(R.id.listArrow) ImageView listArrow;

    public static final int SELECT_LOCATION_REQUEST_CODE = 103;
    public static final String KEY_DELIVERY_TYPE = "delivery.type";

    private SelectLocationAdapter locationAdapter;
    private SelectLocationActivityViewModel viewModel;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private float animStart = 0f;
    private float animEnd = 180f;
    private int menuOpenType = 0;
    private int deliveryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(SelectLocationActivityViewModel.class);
        getDataFromIntent();
        initObservers();

        initSlidingUpPanel();
        getRestaurants();
    }

    private void initObservers() {
        viewModel.getDeliveryZoneLiveData().observe(this, deliveryZone -> handleDeliveryZoneResponse(deliveryZone));
        viewModel.getDeliveryZoneErrorLiveData().observe(this, error -> {
            //TODO Handle delivery zone errors
        });
    }

    private void getDataFromIntent() {
        deliveryType = (getIntent().hasExtra(KEY_DELIVERY_TYPE) ? getIntent().getIntExtra(KEY_DELIVERY_TYPE, 0)
                : Cart.getInstance().getSelectedDeliveryType());
        if (getIntent().hasExtra(INTENT_BUNDLE)) {
            Bundle bundle = getIntent().getBundleExtra(INTENT_BUNDLE);
            if (bundle != null) {
                menuOpenType = bundle.getInt(MENU_OPEN_TYPE);
            }
        }
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLocations);
        mapFragment.getMapAsync(this);
    }

    private void initSlidingUpPanel() {
        slidingUpPanelLayout.setCoveredFadeColor(getResources().getColor(R.color.opacity0));
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setShadowHeight(0);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setPanelHeight(225);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED
                        || newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    flipIt(listArrow);
                }
            }
        });
    }

    private void getRestaurants() {
        if (deliveryType == DeliveryVariant.TYPE_DELIVERY) {
            restaurantList = AvailableRestaurants.getInstance().getDeliveryRestaurantList();
        } else if (deliveryType == DeliveryVariant.TYPE_TABLE_RESERVATION){
            restaurantList = AvailableRestaurants.getInstance().getReservationRestaurantList();
        } else {
            restaurantList = AvailableRestaurants.getInstance().getBaseRestaurantList();
        }
        initLocationsRV();
        initGoogleMap();
    }

    private void initLocationsRV() {
        if (restaurantList != null && restaurantList.size() > 0) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            locationAdapter = new SelectLocationAdapter(this, restaurantList, this);
            locationRV.setLayoutManager(layoutManager);
            locationRV.setItemAnimator(new DefaultItemAnimator());
            locationRV.setAdapter(locationAdapter);
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_restaurant_list_empty), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(8, 8, 8, 230);
        enableMyLocationIfPermitted();
        setupCoordinates();
    }

    private void setupCoordinates() {
        if (restaurantList != null) {
            CustomMarkerHelper.addRestaurantsMarkers(googleMap, getAllRestaurantsLocations());
            if (deliveryType == DeliveryVariant.TYPE_DELIVERY) {
                CustomMarkerHelper.addDeliveryRadiusMarkers(googleMap, getAllRestaurantDeliveryZones(), this);
            }
        }
    }

    private List<Address> getAllRestaurantsLocations() {
        List<Address> addressList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant != null && restaurant.getAddress() != null) {
                addressList.add(restaurant.getAddress());
            }
        }
        return addressList;
    }

    private List<DeliveryZone> getAllRestaurantDeliveryZones() {
        List<DeliveryZone> deliveryZoneList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant != null && !restaurant.getDeliveryZoneList().isEmpty()) {
                deliveryZoneList.addAll(restaurant.getDeliveryZoneList());
            }
        }
        return deliveryZoneList;
    }

    private void handleDeliveryZoneResponse(DeliveryZone deliveryZone) {
        Cart.getInstance().setSelectedDeliveryZone(deliveryZone);
        openMainActivityWithFlag();
    }

    private void openMainActivityWithFlag() {
        CorePreferencesImpl.getCorePreferences().setForceOpenMenu(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    private void flipIt(final View viewToFlip) {
        float animStartBuf = animStart;
        float animEndBuf = animEnd;
        ObjectAnimator flip;
        flip = ObjectAnimator.ofFloat(viewToFlip, "rotationX", animStart, animEnd);
        animStart = animEndBuf;
        animEnd = animStartBuf;
        flip.setDuration(150);
        flip.start();

    }

    @Override
    public void onItemRestaurantClick(int position) {
        Restaurant selectedRestaurant = restaurantList.get(position);
        Cart.getInstance().setSelectedRestaurant(selectedRestaurant);
        CorePreferencesImpl.getCorePreferences().enableLoyalty(selectedRestaurant.isLoyaltyProgramEnable());
        if (menuOpenType != 0) {
            if (Cart.getInstance().getSelectedDeliveryType() == DeliveryVariant.TYPE_DELIVERY) {
                viewModel.findDeliveryZone(selectedRestaurant.getId());
            } else {
                openMainActivityWithFlag();
            }
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Restaurant.RESTAURANT, selectedRestaurant);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

}
