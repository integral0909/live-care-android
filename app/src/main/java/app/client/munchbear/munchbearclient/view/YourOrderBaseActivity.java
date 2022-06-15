package app.client.munchbear.munchbearclient.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferences;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.ORDER_STATUS_ACCEPTED;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.ORDER_STATUS_DECLINE;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.ORDER_STATUS_FAILED;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.ORDER_STATUS_NEW;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.ORDER_STATUS_PENDING;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.RES_STATUS_ACCEPTED;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.RES_STATUS_PENDING;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_DELIVERY;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_EAT_IN;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_TABLE_RESERVATION;
import static app.client.munchbear.munchbearclient.utils.LoginType.GUEST;

public class YourOrderBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupStatusHeader(int status, int id, TextView orderNumber, TextView deliveryStatusTxt,
                                     TextView noRefundTxt, boolean isReservation) {
        orderNumber.setText(String.format(getString(R.string.your_orders_order_number), String.valueOf(id)));
        deliveryStatusTxt.setText(isReservation ? DeliveryVariant.getReservationStatusToString(status, this) :
                DeliveryVariant.getOrderStatusToString(status, this));
        deliveryStatusTxt.setTextColor(isReservation ? DeliveryVariant.getReservationStatusColor(status, this)
                : DeliveryVariant.getOrderStatusColor(status, this));
        noRefundTxt.setVisibility(status == ORDER_STATUS_DECLINE || status == ORDER_STATUS_FAILED ? View.VISIBLE : View.GONE);
    }

    protected void initContactInfoViews(TextView customerName, TextView customerPhone, RoundedImageView customerAvatar,
                               TextView emptyContact, RelativeLayout contactInfoCard, boolean isEmpty, UserData userData) {
        CorePreferences corPref = CorePreferencesImpl.getCorePreferences();
        if (userData != null) {
            contactInfoCard.setVisibility(View.VISIBLE);
            emptyContact.setVisibility(View.GONE);
            customerAvatar.setVisibility(corPref.getLoginType() == GUEST ? View.GONE : View.VISIBLE);
            if (corPref.getLoginType() != GUEST) {
                customerAvatar.setImageBitmap(UserAvatar.getUserAvatar(this));
            }
            customerName.setText(userData.getName());
            customerPhone.setText(userData.getPhone());
        } else {
            emptyContact.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            contactInfoCard.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            customerAvatar.setVisibility(View.GONE);
        }
    }

    protected void setDataToHeaderViews(int status, TextView title, TextView mainText, TextView countTxt) {
        title.setVisibility(status == ORDER_STATUS_NEW ? View.VISIBLE : View.GONE);
        mainText.setVisibility(status == ORDER_STATUS_NEW ? View.GONE : View.VISIBLE );
        if (status == ORDER_STATUS_NEW) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) countTxt.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
            countTxt.setLayoutParams(layoutParams);
            Typeface font = ResourcesCompat.getFont(this, R.font.avenir_next_regular);
            countTxt.setTypeface(font);
        }
    }

    protected void initDeliveryAddress(Address address, ImageView addressArrow, LinearLayout addressFields, TextView emptyAddress,
                                       TextView street, TextView postIndex, TextView cityAndCountry) {
        addressFields.setVisibility(address == null ? View.GONE : View.VISIBLE);
        addressArrow.setVisibility(address == null ? View.GONE : View.VISIBLE);
        emptyAddress.setVisibility(address == null ? View.VISIBLE : View.GONE);

        if (address != null) {
            street.setText(String.format(getResources().getString(R.string.delivery_address_street_and_house),
                    address.getStreet(), address.getHouse()));
            postIndex.setText(address.getPostalCode());
            cityAndCountry.setText(String.format(getResources().getString(R.string.delivery_address_city_and_country),
                    address.getCity(), address.getCountryName()));
        }
    }

    protected void initWhenViews(boolean newReservation, TextView whenDate, TextView whenTime, long resTimestamp) {
        whenDate.setText(newReservation ? getResources().getString(R.string.your_res_empty_date) : DateUtils.getDateMMMMddyyyy(resTimestamp));
        whenTime.setText(newReservation ? getResources().getString(R.string.your_res_empty_time) : DateUtils.getOrderTimeHHmma(resTimestamp));
    }

    protected void initChangeContactButton(TextView contactInfoTitle, LinearLayout orderContactInfoLayout,
                                           String title, TextView nameTV, TextView phoneTV, int status) {
        contactInfoTitle.setText(title);
        orderContactInfoLayout.setOnClickListener(view -> {
            if (status == RES_STATUS_ACCEPTED || status == RES_STATUS_PENDING
                    || status == ORDER_STATUS_NEW) {
                Intent intent = new Intent(this, ChangeProfileActivity.class);
                intent.putExtra(ChangeProfileActivity.SHORT_FORM, true);
                intent.putExtra(UserData.KEY_NAME, nameTV.getText());
                intent.putExtra(UserData.KEY_PHONE, phoneTV.getText());
                startActivityForResult(intent, ChangeProfileActivity.CHANGE_PROFILE_REQUEST_CODE);
            }
        });
    }

    protected void setChangedContactData(TextView nameTextView, TextView phoneTextView, String name, String phone) {
        nameTextView.setText(name);
        phoneTextView.setText(phone);
    }

    protected void initMapViews(LatLng latLng, ImageView deliveryMap, ImageView marker, LinearLayout asSoonLayout, View divide, TextView title, int type) {
        if (latLng != null) {
            CustomMarkerHelper.setLocationPin(marker, type, this);
            GoogleMapHelper.setStaticMap(this, latLng.latitude, latLng.longitude, deliveryMap, true);
        } else {
            deliveryMap.setImageBitmap(GoogleMapHelper.getLogoMockForMaps(this));
        }

        asSoonLayout.setVisibility(type == TYPE_EAT_IN || type == TYPE_TABLE_RESERVATION
                ? View.GONE : View.VISIBLE);
        divide.setVisibility(type == TYPE_EAT_IN || type == TYPE_TABLE_RESERVATION
                ? View.GONE : View.VISIBLE);

        if (type == DeliveryVariant.TYPE_PICK_UP) {
            title.setText(getResources().getString(R.string.your_orders_pick_up_at));
        } else if (type == TYPE_EAT_IN || type == TYPE_TABLE_RESERVATION) {
            title.setText(getResources().getString(R.string.your_orders_eat_in));
        } else {
            title.setText(getResources().getString(R.string.your_orders_delivery_to));
        }
    }

    protected void initDeliveryTabView(LinearLayout tabMain, View tabDivide, int type) {
        tabMain.setVisibility(type == TYPE_TABLE_RESERVATION ? View.GONE : View.VISIBLE);
        tabDivide.setVisibility(type == TYPE_TABLE_RESERVATION ? View.GONE : View.VISIBLE);
    }

    protected void openChangeAddressFragment(int status, int type) {
        if (status == ORDER_STATUS_ACCEPTED || status == ORDER_STATUS_PENDING || status == ORDER_STATUS_NEW) {
            if (type == TYPE_DELIVERY) {
                Intent intent = new Intent(this, NewAddressEditActivity.class);
                intent.putExtra(NewAddressEditActivity.KEY_RETURN_NEW_ADDRESS, true);
                startActivityForResult(intent, NewAddressEditActivity.NEW_ADDRESS_REQUEST_CODE);
            } else {
                Intent intent = new Intent(this, SelectLocationActivity.class);
                if (type == TYPE_TABLE_RESERVATION) {
                    intent.putExtra(SelectLocationActivity.KEY_DELIVERY_TYPE, type);
                }
                startActivityForResult(intent, SelectLocationActivity.SELECT_LOCATION_REQUEST_CODE);
            }
        }
    }

    protected void openMainActivityClearTop(boolean startMenu) {
        CorePreferencesImpl.getCorePreferences().setForceOpenMenu(startMenu);
        CorePreferencesImpl.getCorePreferences().setForceOpenMyOrder(!startMenu);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    protected void openMapDetails(Address address, int deliveryType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailsMapActivity.KEY_ADDRESS, address);
        bundle.putInt(DetailsMapActivity.KEY_DELIVERY_TYPE, deliveryType);
        openActivity(DetailsMapActivity.class, bundle);
    }

}
