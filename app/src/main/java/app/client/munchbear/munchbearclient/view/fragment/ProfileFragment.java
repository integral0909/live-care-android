package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.ChangeProfileActivity;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_EAT_IN;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_TABLE_RESERVATION;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profileLayout) RelativeLayout profileLayout;
    @BindView(R.id.profileGuestLayout) LinearLayout profileGuestLayout;
    @BindView(R.id.addressFields) LinearLayout addressFields;
    @BindView(R.id.logoutBtn) TextView logoutBtn;
    @BindView(R.id.loginSignUpBtn) TextView loginSignUpBtn;
    @BindView(R.id.paymentMethodBtn) RelativeLayout paymentMethodBtn;
    @BindView(R.id.guestInfo) RelativeLayout guestInfo;
    @BindView(R.id.guestInfoTxt) TextView guestInfoTxt;
    @BindView(R.id.profileAvatar) ImageView profileAvatar;
    @BindView(R.id.changeAddressArrow) ImageView changeAddressArrow;
    @BindView(R.id.postIndex) TextView postIndex;
    @BindView(R.id.street) TextView street;
    @BindView(R.id.cityAndCountry) TextView cityAndCountry;
    @BindView(R.id.emptyAddress) TextView emptyAddress;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.nameGuest) TextView nameGuest;
    @BindView(R.id.phone) TextView phone;
    @BindView(R.id.phoneGuest) TextView phoneGuest;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.emailGuest) TextView emailGuest;
    @BindView(R.id.emptyContact) TextView emptyContact;
    @BindView(R.id.contactFields) LinearLayout contactFields;
    @BindView(R.id.map) ImageView mapImg;
    @BindView(R.id.mapMarker) ImageView mapMarker;

    private MainActivityViewModel mainActivityViewModel;
    private Unbinder unbinder;
    private boolean isGuest;
    private UserData userData;
    private Address defaultAddress;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isGuest = CorePreferencesImpl.getCorePreferences().getLoginType() == LoginType.GUEST;
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

        initObservers();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        defaultAddress = CorePreferencesImpl.getCorePreferences().getDefaultDeliveryAddress();
        setupDefaultDeliveryAddress();
        checkGuestViews();

        profileAvatar.setImageBitmap(UserAvatar.getUserAvatar(getContext()));

        return view;
    }

    private void initObservers() {
        mainActivityViewModel.getUserDataLiveData().observe(this, userData -> {
            this.userData = userData;
            initUserDataView();
        });
    }

    private void initUserDataView() {
        emptyContact.setVisibility(userData == null ? View.VISIBLE : View.GONE);
        contactFields.setVisibility(userData == null ? View.GONE : View.VISIBLE);

        if (isGuest) {
            if (userData != null) {
                setUserTextViews(nameGuest, phoneGuest, emailGuest);
            }
        } else {
            setUserTextViews(name, phone, email);
        }
    }

    private void setUserTextViews(TextView name, TextView phone, TextView email) {
        name.setText(userData.getName());
        phone.setText(userData.getPhone());
        email.setText(userData.getEmail());
    }

    private void checkGuestViews() {
        LoyaltyProgram.showProfileHeader(profileGuestLayout, isGuest);
        profileLayout.setVisibility(isGuest ? View.GONE : View.VISIBLE);
        loginSignUpBtn.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        logoutBtn.setVisibility(isGuest ? View.GONE : View.VISIBLE);
        paymentMethodBtn.setVisibility(isGuest ? View.GONE : View.VISIBLE);
        guestInfo.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestInfoTxt.setVisibility(isGuest ? View.VISIBLE : View.GONE);
    }

    private void setupDefaultDeliveryAddress() {
        setupAddressMapView();
        addressFields.setVisibility(defaultAddress == null ? View.GONE : View.VISIBLE);
        changeAddressArrow.setVisibility(defaultAddress == null ? View.GONE : View.VISIBLE);
        emptyAddress.setVisibility(defaultAddress == null ? View.VISIBLE : View.GONE);

        if (defaultAddress != null) {
            street.setText(String.format(getResources().getString(R.string.delivery_address_street_and_house),
                    defaultAddress.getStreet(), defaultAddress.getHouse()));
            postIndex.setText(defaultAddress.getPostalCode());
            cityAndCountry.setText(String.format(getResources().getString(R.string.delivery_address_city_and_country),
                    defaultAddress.getCity(), defaultAddress.getCountryName()));
        }
    }

    private void setupAddressMapView() {
        if (CorePreferencesImpl.getCorePreferences().getDefaultDeliveryAddress() != null) {
            Address address = CorePreferencesImpl.getCorePreferences().getDefaultDeliveryAddress();
            CustomMarkerHelper.setLocationPin(mapMarker, DeliveryVariant.TYPE_DELIVERY, getContext());
            GoogleMapHelper.setStaticMap(getContext(), address.getLat(), address.getLng(), mapImg, true);
        } else {
            mapImg.setImageBitmap(GoogleMapHelper.getLogoMockForMaps(getContext()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userData = CorePreferencesImpl.getCorePreferences().getUserData();
        initUserDataView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.loginSignUpBtn)
    public void clickSignUp() {
        mainActivityViewModel.logout();
    }

    @OnClick(R.id.logoutBtn)
    public void clickLogout() {
        mainActivityViewModel.logout();
    }

    @OnClick(R.id.address)
    public void clickDeliveryAddress() {
        mainActivityViewModel.openDeliveryAddress();
    }

    @OnClick(R.id.guestInfo)
    public void clickChangeGuestProfile() {
        openActivity(getContext(), ChangeProfileActivity.class);
    }

    @OnClick(R.id.profileLayout)
    public void clickChangeUserProfile() {
        openActivity(getContext(), ChangeProfileActivity.class);
    }

    @OnClick(R.id.paymentMethodBtn)
    public void clickPaymentMethod() {
        mainActivityViewModel.openPaymentMethod();
    }
}
