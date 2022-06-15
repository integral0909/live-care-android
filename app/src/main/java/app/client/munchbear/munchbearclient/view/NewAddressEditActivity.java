package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.ValidationsUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.fragment.DeliveryAddressFragment;
import app.client.munchbear.munchbearclient.viewmodel.NewAddressEditActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.view.fragment.BaseFragment.INTENT_BUNDLE;
import static app.client.munchbear.munchbearclient.view.fragment.PreMenuFragment.MENU_OPEN_TYPE;
import static app.client.munchbear.munchbearclient.view.fragment.PreMenuFragment.TYPE_NEW_DELIVERY_ADDRESS;

public class NewAddressEditActivity extends UserLocationBaseActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    @BindView(R.id.editStreet) TextInputLayout editStreet;
    @BindView(R.id.editHouseNumber) TextInputLayout editHouseNumber;
    @BindView(R.id.editUnitNumber) TextInputLayout editUnitNumber;
    @BindView(R.id.editZipCode) TextInputLayout editZipCode;
    @BindView(R.id.selectAddressLayout) RelativeLayout selectAddressLayout;
    @BindView(R.id.container) FrameLayout container;

    public static final int NEW_ADDRESS_REQUEST_CODE = 106;

    public static final String KEY_RETURN_NEW_ADDRESS = "key.return.new.address";

    private NewAddressEditActivityViewModel newAddressEditActivityViewModel;

    private boolean returnAddress = false;
    private int menuOpenType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address_edit);
        ButterKnife.bind(this);

        newAddressEditActivityViewModel = ViewModelProviders.of(this).get(NewAddressEditActivityViewModel.class);

        getDataFromIntent();
        initObservers();
        setupSelectAddressView();
        initGoogleMap();

    }

    private void setupSelectAddressView() {
        selectAddressLayout.setVisibility(CorePreferencesImpl.getCorePreferences().getDefaultDeliveryAddress() == null ? View.GONE : View.VISIBLE);
    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra(INTENT_BUNDLE)) {
            Bundle bundle = getIntent().getBundleExtra(INTENT_BUNDLE);
            if (bundle != null) {
                menuOpenType = bundle.getInt(MENU_OPEN_TYPE, 0);
            }
        }
        if (getIntent().hasExtra(KEY_RETURN_NEW_ADDRESS)) {
            returnAddress = getIntent().getBooleanExtra(KEY_RETURN_NEW_ADDRESS, false);
        }
    }

    private void initObservers() {
        newAddressEditActivityViewModel.getSaveAddressLiveData().observe(this, aBoolean -> {
            if (aBoolean) {
                if (menuOpenType != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MENU_OPEN_TYPE, menuOpenType);
                    openActivity(SelectLocationActivity.class, bundle);
                } else {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        });

        newAddressEditActivityViewModel.getReturnAddressLiveData().observe(this, returnAddress -> {
            if (returnAddress != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Address.ADDRESS, returnAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void setAddressFields() {
        super.setAddressFields();
        editStreet.getEditText().setText(pinedAddress.getStreet());
        editZipCode.getEditText().setText(pinedAddress.getPostalCode());
        editHouseNumber.getEditText().setText(pinedAddress.getHouse());
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocationIfPermitted();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        googleMap.clear();
        CustomMarkerHelper.addCustomUserMarker(googleMap, latLng, this, false);
        pinedAddress = GoogleMapHelper.getAddressDataList(this, latLng.latitude, latLng.longitude);
        setAddressFields();
    }

    @OnClick(R.id.saveBtn)
    public void saveAddressData() {
        clearErrors();

        String street = editStreet.getEditText().getText().toString().trim();
        String houseNumber = editHouseNumber.getEditText().getText().toString().trim();
        String unitNumber = editUnitNumber.getEditText().getText().toString().trim();
        String zipCode = editZipCode.getEditText().getText().toString().trim();

        if (validateFields(street, houseNumber, zipCode)) {
            if (menuOpenType == TYPE_NEW_DELIVERY_ADDRESS) {
                newAddressEditActivityViewModel.saveAddressForCurrentOrder(this, pinedAddress.getCity(), pinedAddress.getCountryName(),
                        street, houseNumber, unitNumber, zipCode);
            } else {
                if (returnAddress) {
                    newAddressEditActivityViewModel.returnNewAddress(pinedAddress.getCity(), pinedAddress.getCountryName(),
                            street, houseNumber, unitNumber, zipCode, pinedAddress.getLat(), pinedAddress.getLng());
                } else {
                    newAddressEditActivityViewModel.saveAddress(pinedAddress.getCity(), pinedAddress.getCountryName(),
                            street, houseNumber, unitNumber, zipCode, pinedAddress.getLat(), pinedAddress.getLng());
                }
            }
        }
    }

    private boolean validateFields(String street, String houseNumber, String zipCode) {
        return ValidationsUtils.isEmpty(editStreet, street, getResources().getString(R.string.empty_fields))
                && ValidationsUtils.isEmpty(editHouseNumber, houseNumber, getResources().getString(R.string.empty_fields))
                && ValidationsUtils.isEmpty(editZipCode, zipCode, getResources().getString(R.string.empty_fields));
    }

    private void clearErrors() {
        editStreet.setError(null);
        editHouseNumber.setError(null);
        editUnitNumber.setError(null);
        editZipCode.setError(null);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.selectAddressFromMy)
    public void clickSelectFromMyAddress() {
        replaceFragment(DeliveryAddressFragment.newInstance(false), true, R.id.container, true, false);
    }

    public void onSelectedAddress() {
        pinedAddress = CorePreferencesImpl.getCorePreferences().getDefaultDeliveryAddress();
        LatLng latLng = new LatLng(pinedAddress.getLat(), pinedAddress.getLng());
        setAddressFields();
        googleMap.clear();
        CustomMarkerHelper.addCustomUserMarker(googleMap, latLng, this, true);
        onBackPressed();
    }

}
