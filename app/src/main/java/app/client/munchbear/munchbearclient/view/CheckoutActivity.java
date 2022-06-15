package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.OrderRequestBuilder;
import app.client.munchbear.munchbearclient.utils.TextUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.CheckoutActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_DELIVERY;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_EAT_IN;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_PICK_UP;
import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_COMMENT;
import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_NUMBER_OF_TABLE;
import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_PROMOCODE;

public class CheckoutActivity extends YourOrderBaseActivity {

    @BindView(R.id.customerAvatar) RoundedImageView customerAvatar;
    @BindView(R.id.customerName) TextView customerName;
    @BindView(R.id.customerPhone) TextView customerPhone;
    @BindView(R.id.contactInfoCard) RelativeLayout contactInfoCard;
    @BindView(R.id.orderContactInfoLayout) LinearLayout orderContactInfoLayout;
    @BindView(R.id.contactInfoTitle) TextView contactInfoTitle;

    @BindView(R.id.deliveryToTitle) TextView deliveryToTitle;
    @BindView(R.id.hintViewPager) ViewPager hintViewPager;
    @BindView(R.id.deliveryMap) ImageView deliveryMap;
    @BindView(R.id.mapMarker) ImageView mapMarker;

    @BindView(R.id.asSoonLayout) LinearLayout asSoonLayout;
    @BindView(R.id.asSoonTxt) TextView asSoonTxt;
    @BindView(R.id.deliveryTimeDate) TextView deliveryTimeDate;
    @BindView(R.id.deliveryTimeTime) TextView deliveryTimeTime;

    @BindView(R.id.numberOfTable) TextView numberOfTable;
    @BindView(R.id.changeTableNumber) TextView changeTableNumber;
    @BindView(R.id.orderTableNumberLayout) LinearLayout orderTableNumberLayout;
    @BindView(R.id.tableInfoLayout) RelativeLayout tableInfoLayout;
    @BindView(R.id.emptyTable) RelativeLayout emptyTable;

    @BindView(R.id.promocodeTxt) TextView promocodeTxt;
    @BindView(R.id.emptyPromoCode) RelativeLayout emptyPromoCode;
    @BindView(R.id.promoCodeInfoLayout) RelativeLayout promoCodeInfoLayout;

    @BindView(R.id.commentTxt) TextView commentTxt;

    @BindView(R.id.emptyAddress) TextView emptyAddress;
    @BindView(R.id.addressFields) LinearLayout addressFields;
    @BindView(R.id.arrowChangeAddress) ImageView arrowChangeAddress;
    @BindView(R.id.postIndex) TextView postIndex;
    @BindView(R.id.street) TextView street;
    @BindView(R.id.cityAndCountry) TextView cityAndCountry;

    @BindView(R.id.emptyContact) TextView emptyContact;

    @BindView(R.id.changePaymentMethod) TextView changePaymentMethod;
    @BindView(R.id.paymentMethodLayout) RelativeLayout paymentMethodLayout;
    @BindView(R.id.emptyPaymentMethod) RelativeLayout emptyPaymentMethod;
    @BindView(R.id.paymentMethodIcon) ImageView paymentMethodIcon;
    @BindView(R.id.hDots) ImageView hDots;
    @BindView(R.id.paymentType) TextView paymentTypeTxt;

    @BindView(R.id.tabTitle1) TextView tabTitle1;
    @BindView(R.id.tabTitle2) TextView tabTitle2;
    @BindView(R.id.tabTitle3) TextView tabTitle3;
    @BindView(R.id.underLine1) View underLine1;
    @BindView(R.id.underLine2) View underLine2;
    @BindView(R.id.underLine3) View underLine3;

    @BindView(R.id.itemCount) TextView itemCount;
    @BindView(R.id.totalSumDollar) TextView totalSumDollar;
    @BindView(R.id.totalSumPoint) TextView totalSumPoint;
    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.orTxt) TextView orTxt;

    private CheckoutActivityViewModel checkoutActivityViewModel;
    private Address address;
    private Restaurant restaurant;
    private Address restaurantAddress;
    private int deliveryType;
    private int deliveryTimeType = DeliveryTimeActivity.TYPE_SPECIFIC_TIME;
    private int loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        OrderRequestBuilder.getInstance().initOrderBuilder();

        checkoutActivityViewModel = ViewModelProviders.of(this).get(CheckoutActivityViewModel.class);
        initObservers();

        address = Cart.getInstance().getSelectedDeliveryAddress();
        restaurant = Cart.getInstance().getSelectedRestaurant();
        initRestaurant(restaurant);

        deliveryType = Cart.getInstance().getSelectedDeliveryType();
        loginType = CorePreferencesImpl.getCorePreferences().getLoginType();
        initViews();
    }

    private void initObservers() {
        checkoutActivityViewModel.getCreateOrderLiveData().observe(this, created -> {
            if (created) {
                Cart.clearCart();
                openMainActivityClearTop(false);
            }
        });

        checkoutActivityViewModel.getCreateOrderErrorLiveData().observe(this, error -> {

        });
    }

    private void initViews() {
        initContactInfoViews(customerName, customerPhone, customerAvatar, emptyContact, contactInfoCard,
                true, CorePreferencesImpl.getCorePreferences().getUserData());
        initChangeContactButton(contactInfoTitle, orderContactInfoLayout, getResources().getString(R.string.your_orders_contact_info)
                , customerName, customerPhone, DeliveryVariant.ORDER_STATUS_NEW);
        initDeliveryViews();
        initPaymentMethodViews(true);
        setupOrderTotal();
        initEmptyView(emptyPromoCode, promoCodeInfoLayout, true);
        initTableNumberViews(true);
        initCommentViews();
        LoyaltyProgram.setupCartViews(orTxt, imageStar, totalSumPoint, totalSumDollar);
    }

    private void initEmptyView(RelativeLayout emptyLayout, RelativeLayout infoLayout, boolean emptyState) {
        infoLayout.setVisibility(emptyState ? View.GONE : View.VISIBLE);
        emptyLayout.setVisibility(emptyState ? View.VISIBLE : View.GONE);
    }

    private void initDeliveryViews() {
        initDeliveryTypeTab();
        deliveryToTitle.setVisibility(View.GONE);
    }

    private void initPaymentMethodViews(boolean emptyState) {
        changePaymentMethod.setVisibility(View.VISIBLE);
        initEmptyView(emptyPaymentMethod, paymentMethodLayout, emptyState);
    }

    private void initTableNumberViews(boolean emptyState) {
        changeTableNumber.setVisibility(View.VISIBLE);
        initEmptyView(emptyTable, tableInfoLayout, emptyState);
    }

    private void initCommentViews() {
        commentTxt.setText(getResources().getString(R.string.your_orders_comment));
        commentTxt.setTextColor(getResources().getColor(R.color.bottomMenuDarkGrey));
    }

    private void initDeliveryTypeTab() {
        switch (deliveryType) {
            case TYPE_DELIVERY:
                setupDeliveryTab(address);
                deliveryType = TYPE_DELIVERY;
                tabTitle1.setTextColor(getResources().getColor(R.color.darkGrey));
                underLine1.setVisibility(View.VISIBLE);
                break;
            case TYPE_PICK_UP:
                setupDeliveryTab(restaurantAddress);
                tabTitle2.setTextColor(getResources().getColor(R.color.darkGrey));
                underLine2.setVisibility(View.VISIBLE);
                break;
            case TYPE_EAT_IN:
                setupDeliveryTab(restaurantAddress);
                tabTitle3.setTextColor(getResources().getColor(R.color.darkGrey));
                underLine3.setVisibility(View.VISIBLE);
                break;
        }

        deliveryTimeType = (deliveryType == 0 ? DeliveryTimeActivity.TYPE_AS_SOON : DeliveryTimeActivity.TYPE_NONE_DELIVERY_TYPE);
        emptyAddress.setText(deliveryType == 0 ? getResources().getString(R.string.profile_empty_address)
                : getResources().getString(R.string.your_res_select_restaurant));
        orderTableNumberLayout.setVisibility(deliveryType == TYPE_EAT_IN ? View.VISIBLE : View.GONE);
        asSoonLayout.setVisibility(deliveryType == TYPE_EAT_IN ? View.GONE : View.VISIBLE);
    }

    private void setupDeliveryTab(Address address) {
        CustomMarkerHelper.setLocationPin(mapMarker, deliveryType, this);
        GoogleMapHelper.setStaticMap(this, address.getLat(), address.getLng(), deliveryMap, true);
        initDeliveryAddress(address, arrowChangeAddress, addressFields, emptyAddress, street, postIndex, cityAndCountry);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ChangeDataActivity.CHANGE_DATA_REQUEST_CODE:
                    int changeType = data.getIntExtra(ChangeDataActivity.CHANGE_TYPE, 1);
                    setChangedData(changeType, data.getStringExtra(ChangeDataActivity.RESULT_VALUE));
                    break;
                case ChangeProfileActivity.CHANGE_PROFILE_REQUEST_CODE:
                    setChangedContactData(customerName, customerPhone,
                            data.getStringExtra(UserData.KEY_NAME), data.getStringExtra(UserData.KEY_PHONE));
                    OrderRequestBuilder.getInstance().setUserContact(customerName.getText().toString(), customerPhone.getText().toString());
                    break;
                case SelectPayMethodActivity.SELECT_PAYMENT_REQUEST_CODE:
                    String paymentType = data.getStringExtra(SelectPayMethodActivity.KEY_PAYMENT_TYPE);
                    int cardId = data.getIntExtra(SelectPayMethodActivity.KEY_CARD_ID, 0); //TODO Card if only for order request
                    String cardLastNumber = data.getStringExtra(SelectPayMethodActivity.KEY_CARD_LAST_NUMBERS);
                    getPaymentMethod(paymentType, cardLastNumber);
                    OrderRequestBuilder.getInstance().setPaymentType(paymentType);
                    break;
                case DeliveryTimeActivity.DELIVERY_TIME_REQUEST_CODE:
                    deliveryTimeType = data.getIntExtra(DeliveryTimeActivity.KEY_DELIVERY_TIME_TYPE, DeliveryTimeActivity.TYPE_NONE_DELIVERY_TYPE);
                    checkoutActivityViewModel.handleDeliveryTime(deliveryTimeType, data, deliveryTimeDate, deliveryTimeTime);
                    asSoonTxt.setVisibility(deliveryTimeType == DeliveryTimeActivity.TYPE_SPECIFIC_TIME ? View.GONE : View.VISIBLE);
                    break;
            }
        }
    }

    private void initRestaurant(Restaurant restaurant) {
        if (restaurant != null) {
            restaurantAddress = restaurant.getAddress();
        }
    }

    private void setChangedData(int changeType, String value) {
        switch (changeType) {
            case CHANGE_NUMBER_OF_TABLE:
                numberOfTable.setText(value);
                initTableNumberViews(false);
                break;
            case CHANGE_PROMOCODE:
                promocodeTxt.setText(value);
                initEmptyView(emptyPromoCode, promoCodeInfoLayout, false);
                break;
            case CHANGE_COMMENT:
                commentTxt.setText(value);
                commentTxt.setTextColor(getResources().getColor(R.color.darkGrey));
                OrderRequestBuilder.getInstance().setComment(value);
                break;
        }
    }

    private void getPaymentMethod(String paymentMethodType, String cardLastNumber) {
        switch (paymentMethodType) {
            case SelectPayMethodActivity.PAYMENT_METHOD_CASH:
                setDataToPaymentViews(paymentMethodType, getResources().getDrawable(R.mipmap.ic_dollar),
                        getResources().getString(R.string.your_orders_cash));
                break;
            case SelectPayMethodActivity.PAYMENT_METHOD_LOYALTY:
                setDataToPaymentViews(paymentMethodType, getResources().getDrawable(R.mipmap.star_black),
                        getResources().getString(R.string.pay_method_loyalty_points));
                break;
            case SelectPayMethodActivity.PAYMENT_METHOD_CARD:
                setDataToPaymentViews(paymentMethodType, getResources().getDrawable(R.mipmap.payment_method), cardLastNumber);
                break;
        }
    }

    private void setDataToPaymentViews(String paymentMethodType, Drawable icon, String typeText) {
        initPaymentMethodViews(false);
        hDots.setVisibility(paymentMethodType.equals(SelectPayMethodActivity.PAYMENT_METHOD_CARD) ? View.VISIBLE : View.GONE);
        paymentMethodIcon.setImageDrawable(icon);
        paymentTypeTxt.setText(typeText);
    }

    private void setupOrderTotal() {
        itemCount.setText(String.format(getResources().getString(R.string.checkout_total_item),
                String.valueOf(Cart.getInstance().getDishList().size())));
        TextUtils.setupTotalPrice(this, totalSumDollar, totalSumPoint, Cart.getInstance().getTotalPriceDollar(),
                Cart.getInstance().getTotalPricePoint(), 1);
    }

    private void setOrderAddress() {
        OrderRequestBuilder orderRequestBuilder = OrderRequestBuilder.getInstance();
        if (deliveryType == TYPE_DELIVERY) {
            orderRequestBuilder.setOrderLocation(address);
        } else if (deliveryType == TYPE_EAT_IN) {
            orderRequestBuilder.setOrderLocation(Integer.parseInt(numberOfTable.getText().toString()));
        }
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.rootPromoCodeLayout)
    public void clickPromoCode() {
        openChangeDataActivity(ChangeDataActivity.CHANGE_PROMOCODE);
    }

    @OnClick(R.id.orderCommentLayout)
    public void clickComment() {
        openChangeDataActivity(ChangeDataActivity.CHANGE_COMMENT);
    }

    @OnClick(R.id.orderTableNumberLayout)
    public void clickTableNumber() {
        openChangeDataActivity(CHANGE_NUMBER_OF_TABLE);
    }

    @OnClick(R.id.asSoonLayout)
    public void clickAsSoon() {
        startActivityForResult(new Intent(this, DeliveryTimeActivity.class), DeliveryTimeActivity.DELIVERY_TIME_REQUEST_CODE);
    }

    @OnClick(R.id.rootPaymentLayout)
    public void clickPayMethod() {
        startActivityForResult(new Intent(this, SelectPayMethodActivity.class), SelectPayMethodActivity.SELECT_PAYMENT_REQUEST_CODE);
    }

    @OnClick(R.id.checkoutBtn)
    public void checkout() {
        setOrderAddress();
        checkoutActivityViewModel.createOrder(OrderRequestBuilder.getInstance().getMakeOrder(), this);
    }

}
