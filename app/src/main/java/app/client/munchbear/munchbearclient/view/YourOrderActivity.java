package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.OrderDetails;
import app.client.munchbear.munchbearclient.model.order.OrderUserContact;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.view.adapter.CreatedOrderDishAdapter;
import app.client.munchbear.munchbearclient.viewmodel.YourOrderActivityViewModel;
import butterknife.BindView;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.model.order.make.MakeOrder.ORDER_ID;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_DELIVERY;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_EAT_IN;
import static app.client.munchbear.munchbearclient.utils.DeliveryVariant.TYPE_PICK_UP;
import static app.client.munchbear.munchbearclient.view.SelectPayMethodActivity.PAYMENT_METHOD_CARD;
import static app.client.munchbear.munchbearclient.view.SelectPayMethodActivity.PAYMENT_METHOD_CASH;
import static app.client.munchbear.munchbearclient.view.SelectPayMethodActivity.PAYMENT_METHOD_LOYALTY;

public class YourOrderActivity extends YourOrderBaseActivity {

    private DecimalFormat decim = new DecimalFormat("0.00");

    @BindView(R.id.itemOrderList) RecyclerView itemOrderRV;
    @BindView(R.id.minsPostFix) TextView minsPostFix;
    @BindView(R.id.minsTxt) TextView minsTxt;
    @BindView(R.id.orderNumber) TextView orderNumber;
    @BindView(R.id.noRefundTxt) TextView noRefundTxt;
    @BindView(R.id.customerName) TextView customerName;
    @BindView(R.id.customerPhone) TextView customerPhone;
    @BindView(R.id.changeContact) TextView changeContact;
    @BindView(R.id.deliveryStatusTxt) TextView deliveryStatusTxt;
    @BindView(R.id.deliveryToTitle) TextView deliveryToTitle;
    @BindView(R.id.placedTime) TextView placedTime;
    @BindView(R.id.deliveryTabMainLayout) LinearLayout deliveryTabMainLayout;
    @BindView(R.id.orderWillDeliveredTitle) TextView orderWillDeliveredTitle;
    @BindView(R.id.customerAvatar) RoundedImageView customerAvatar;
    @BindView(R.id.deliveryMap) ImageView deliveryMap;
    @BindView(R.id.asSoonLayout) LinearLayout asSoonLayout;
    @BindView(R.id.orderTableNumberLayout) LinearLayout orderTableNumberLayout;
    @BindView(R.id.divide1) View divide1;
    @BindView(R.id.divide2) View divide2;
    @BindView(R.id.orderWillDeliveredLayout) ConstraintLayout orderWillDeliveredLayout;
    @BindView(R.id.container) FrameLayout container;
    @BindView(R.id.totalSumDollar) TextView totalSumDollar;
    @BindView(R.id.totalSumPoint) TextView totalSumPoint;
    @BindView(R.id.itemCount) TextView itemCount;
    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.mapMarker) ImageView mapMarker;

    @BindView(R.id.emptyAddress) TextView emptyAddress;
    @BindView(R.id.addressFields) LinearLayout addressFields;
    @BindView(R.id.postIndex) TextView postIndex;
    @BindView(R.id.street) TextView street;
    @BindView(R.id.arrowChangeAddress) ImageView arrowChangeAddress;
    @BindView(R.id.cityAndCountry) TextView cityAndCountry;

    @BindView(R.id.paymentMethodIcon) ImageView paymentMethodIcon;
    @BindView(R.id.hDots) ImageView hDots;
    @BindView(R.id.paymentType) TextView paymentTypeText;
    @BindView(R.id.commentTxt) TextView commentTxt;
    @BindView(R.id.numberOfTable) TextView numberOfTable;
    @BindView(R.id.orderCommentLayout) LinearLayout orderCommentLayout;

    private YourOrderActivityViewModel yourOrderActivityViewModel;
    private CreatedOrderDishAdapter dishAdapter;
    private OrderDetails order;
    private OrderUserContact customerData;
    private String orderId;
    private int status;
    private int deliveryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_order);
        ButterKnife.bind(this);

        yourOrderActivityViewModel = ViewModelProviders.of(this).get(YourOrderActivityViewModel.class);
        initObservers();

        startGetOrderDetails();
    }

    private void initObservers() {
        yourOrderActivityViewModel.getOrderDetailsLiveData().observe(this, orderDetails -> handleOrderDetails(orderDetails));
    }

    private void handleOrderDetails(OrderDetails orderDetails) {
        order = orderDetails;
        customerData = orderDetails.getUserContact();
        status = orderDetails.getStatus();
        deliveryType = DeliveryVariant.convertDeliveryTypeStringToInt(orderDetails.getOrderDelivery().getType());

        setupViews();
    }

    private void startGetOrderDetails() {
        if (hasIntentOrderId() && !TextUtils.isEmpty(orderId)) {
            yourOrderActivityViewModel.getOrderDetailsById(orderId);
        }
    }

    private boolean hasIntentOrderId() {
        if (getIntent().hasExtra(ORDER_ID)) {
            orderId = getIntent().getStringExtra(ORDER_ID);
            return true;
        } else {
            return false;
        }
    }

    private void setupViews() {
        if (order != null) {
            setupStatusHeader(order.getStatus(), order.getNumber(), orderNumber, deliveryStatusTxt, noRefundTxt, false);
            setOrderTimes();
            setupDeliveryTab();
            setupContactInfoViews();
            setupOrderTotal();
            setupPaymentMethod("1488"); // TODO Add card number when server will be ready
            initItemOrderList();
            setupComment();
            setNumberOfTable();

            deliveryTabMainLayout.setVisibility(View.GONE);
            changeContact.setVisibility(View.GONE);
            orderTableNumberLayout.setVisibility(deliveryType == DeliveryVariant.TYPE_EAT_IN ? View.VISIBLE : View.GONE);

            if (status == DeliveryVariant.ORDER_STATUS_ACCEPTED || status == DeliveryVariant.ORDER_STATUS_PENDING) {
                arrowChangeAddress.setVisibility(View.VISIBLE);
                orderWillDeliveredTitle.setText(status == DeliveryVariant.ORDER_STATUS_ACCEPTED ? getResources().getString(R.string.my_orders_order_wiil_be)
                        : getResources().getString(R.string.my_orders_order_wiil_be_ready));
                divide1.setVisibility(View.VISIBLE);
            } else {
                arrowChangeAddress.setVisibility(View.GONE);
                divide1.setVisibility(View.GONE);
            }
        }
    }

    private void setupDeliveryTab() {
        LatLng latLng = order.getOrderDelivery().getLatLng();
        initMapViews(latLng, deliveryMap, mapMarker, asSoonLayout, divide2, deliveryToTitle, deliveryType);

        switch (deliveryType) {
            case TYPE_DELIVERY:
                postIndex.setVisibility(View.GONE);
                cityAndCountry.setVisibility(View.GONE);
                street.setText(order.getOrderDelivery().getOrderLocation().getAddress());
                break;
            case TYPE_EAT_IN:
                initDeliveryAddress(order.getOrderDelivery().getOrderLocation().getRestaurant().getAddress(), arrowChangeAddress, addressFields, emptyAddress, street, postIndex, cityAndCountry);
                break;
            case TYPE_PICK_UP:
                initDeliveryAddress(order.getOrderDelivery().getOrderLocation(), arrowChangeAddress, addressFields, emptyAddress, street, postIndex, cityAndCountry);
                break;
        }
    }

    private void setOrderTimes() {
        placedTime.setText(DateUtils.getOrderPlacedTime(order.getCreated_at()));
        orderWillDeliveredLayout.setVisibility(order.showReadyTime() ? View.VISIBLE : View.GONE);
        minsTxt.setText(order.getReadyTimeString(false));
        minsPostFix.setVisibility(order.getReadyTimeString(false).equals("Late") ? View.GONE : View.VISIBLE);
    }

    private void setupOrderTotal() {
        String totalDollarStr = "$" + decim.format(order.getTotalInDollar());
        String itemCountStr = order.getCreatedOrderItemsList().size() + " items";

        totalSumDollar.setVisibility(isPaymentLoyaltyPoints() ? View.GONE : View.VISIBLE);
        totalSumPoint.setVisibility(isPaymentLoyaltyPoints() ? View.VISIBLE : View.GONE);
        imageStar.setVisibility(isPaymentLoyaltyPoints() ? View.VISIBLE : View.GONE);

        totalSumPoint.setText(String.valueOf(order.getTotal()));
        totalSumDollar.setText(totalDollarStr);
        itemCount.setText(itemCountStr);
    }

    private void setupPaymentMethod(String cardNumber) {
        switch (order.getPaymentType()) {
            case PAYMENT_METHOD_CASH:
                setDataToPaymentViews(order.getPaymentType(), getResources().getDrawable(R.mipmap.ic_dollar),
                        getResources().getString(R.string.your_orders_cash));
                break;
            case PAYMENT_METHOD_LOYALTY:
                setDataToPaymentViews(order.getPaymentType(), getResources().getDrawable(R.mipmap.star_black),
                        getResources().getString(R.string.pay_method_loyalty_points));
                break;
            case PAYMENT_METHOD_CARD:
                setDataToPaymentViews(order.getPaymentType(), getResources().getDrawable(R.mipmap.payment_method), cardNumber);
                break;
        }
    }

    private void setupComment() {
        boolean hasComment = !TextUtils.isEmpty(order.getComment());
        orderCommentLayout.setVisibility(hasComment ? View.VISIBLE : View.GONE);
        commentTxt.setText(hasComment ? order.getComment() : "");
    }

    private void setDataToPaymentViews(String paymentMethodType, Drawable icon, String typeText) {
        hDots.setVisibility(paymentMethodType.equals(PAYMENT_METHOD_CARD) ? View.VISIBLE : View.GONE);
        paymentMethodIcon.setImageDrawable(icon);
        paymentTypeText.setText(typeText);
    }

    private void setupContactInfoViews() {
        if(customerData != null) {
            customerAvatar.setVisibility(View.GONE);
            customerName.setText(customerData.getUserName());
            customerPhone.setText(customerData.getUserPhone());
        }
    }

    private boolean isPaymentLoyaltyPoints() {
        return order.getPaymentType().equals(PAYMENT_METHOD_LOYALTY);
    }

    private void setNumberOfTable() {
        if (order.getOrderDelivery().getType().equals(DeliveryVariant.EAT_IN)) {
            numberOfTable.setText(String.valueOf(order.getOrderDelivery().getOrderLocation().getTableNumber()));
        }
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    private void initItemOrderList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dishAdapter = new CreatedOrderDishAdapter(this, order.getPaymentType(), order.getCreatedOrderItemsList());
        itemOrderRV.setLayoutManager(layoutManager);
        itemOrderRV.setItemAnimator(new DefaultItemAnimator());
        itemOrderRV.setAdapter(dishAdapter);
    }

}
