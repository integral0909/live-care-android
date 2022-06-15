package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrder;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder> {

    private DecimalFormat decim = new DecimalFormat("0.00");

    private Context context;
    private List<CreatedOrder> orderList;
    private List<CreatedReservation> createdReservationList;
    private ItemClickListener itemClickListener;
    private boolean isReservation;
    private boolean reservationLastPage = true;
    private boolean orderLastPage = true;

    public MyOrderAdapter(Context context, List<CreatedOrder> orderList, List<CreatedReservation> createdReservationList,
                          ItemClickListener listener, boolean isReservation) {
        this.context = context;
        this.orderList = orderList;
        this.createdReservationList = createdReservationList;
        this.itemClickListener = listener;
        this.isReservation = isReservation;
    }

    @Override
    public MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_order_item, parent, false);

        return new MyOrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyOrderViewHolder holder, int position) {
        if(isReservation) {
            holder.bindReservationData(createdReservationList.get(position));
        } else {
            holder.bindOrderData(orderList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return isReservation ? createdReservationList.size() : orderList.size();
    }

    public void setReservationLastPage(boolean reservationLastPage) {
        this.reservationLastPage = reservationLastPage;
    }

    public void setOrderLastPage(boolean orderLastPage) {
        this.orderLastPage = orderLastPage;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class MyOrderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mapImg) ImageView mapImg;
        @BindView(R.id.mapMarker) ImageView mapMarker;
        @BindView(R.id.status) TextView deliveryStatus;
        @BindView(R.id.deliveryType) TextView deliveryType;
        @BindView(R.id.totalSum) TextView totalSum;
        @BindView(R.id.date) TextView reservationDate;
        @BindView(R.id.readyInTime) TextView readyInTime;
        @BindView(R.id.orderPlacedTime) TextView orderPlacedTime;
        @BindView(R.id.orderNumber) TextView orderNumber;
        @BindView(R.id.tablePlaced) RelativeLayout tablePlaced;
        @BindView(R.id.orderPlaced) RelativeLayout orderPlaced;
        @BindView(R.id.deliveredIn) RelativeLayout deliveredIn;
        @BindView(R.id.progressBar) ProgressBar progressBar;
        @BindView(R.id.total) RelativeLayout total;

        public MyOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindReservationData(CreatedReservation createdReservation) {
            if (createdReservation != null) {
                initViewsVisibility(true);
                setupInfo(createdReservation.getStatus(), DeliveryVariant.TYPE_TABLE_RESERVATION, createdReservation.getId());
                setupRestaurantStaticMap(createdReservation);
                reservationDate.setText(DateUtils.getDateMMMMddyyyyhhmma(createdReservation.getWhen()));
            }
        }

        public void bindOrderData(CreatedOrder order) {
            if (order != null) {
                initViewsVisibility(false);
                totalSum.setText(String.format(context.getString(R.string.my_orders_order_price), decim.format(order.getTotalInDollar()))); //TODO Add implementation for loyalty points!!!!
                orderPlacedTime.setText(DateUtils.getOrderPlacedTime(order.getCreated_at()));
                setupInfo(order.getStatus(), DeliveryVariant.convertDeliveryTypeStringToInt(order.getOrderDelivery().getType()), order.getNumber());
                setupOrderStaticMap(order);
                setReadyTime(order);
            }
        }

        private void setupInfo(int status, int type, int orderId) {
            deliveryStatus.setText(DeliveryVariant.getStatusToString(type, status, context));
            deliveryStatus.setBackground(DeliveryVariant.getStatusDrawable(status, isReservation, context));
            deliveryType.setText(DeliveryVariant.getTypeToString(type, context));
            orderNumber.setText(String.format(context.getString(R.string.your_orders_order_number2), String.valueOf(orderId)));
            CustomMarkerHelper.setLocationPin(mapMarker, type, context);
        }

        private void setupRestaurantStaticMap(@NonNull CreatedReservation createdReservation) {
            Address address = createdReservation.getRestaurant().getAddress();
            GoogleMapHelper.setStaticMap(context, address.getLat(), address.getLng(), mapImg, false);
        }

        private void setupOrderStaticMap(@NonNull CreatedOrder createdOrder) {
            LatLng latLng = createdOrder.getOrderDelivery().getLatLng();
            GoogleMapHelper.setStaticMap(context, latLng.latitude, latLng.longitude, mapImg, false);
        }

        private void setReadyTime(@NonNull CreatedOrder createdOrder) {
            deliveredIn.setVisibility(createdOrder.showReadyTime() ? View.VISIBLE : View.GONE);
            readyInTime.setText(createdOrder.showReadyTime() ? createdOrder.getReadyTimeString(true) : "");
        }

        private void initViewsVisibility(boolean isTableReservation) {
            tablePlaced.setVisibility(isTableReservation ? View.VISIBLE : View.GONE);
            orderPlaced.setVisibility(isTableReservation ? View.GONE : View.VISIBLE);
            deliveredIn.setVisibility(isTableReservation ? View.GONE : View.VISIBLE);
            total.setVisibility(isTableReservation ? View.GONE : View.VISIBLE);
            setupFooterProgressBar();
        }

        private void setupFooterProgressBar() {
            boolean lastPage = isReservation ? reservationLastPage : orderLastPage;
            progressBar.setVisibility(!lastPage && getItemCount() - 1 == getAdapterPosition() ?
                    View.VISIBLE : View.GONE);
        }

        @OnClick(R.id.rootLayout)
        public void clickItem() {
            itemClickListener.onItemClick(this.getLayoutPosition());
        }
    }

}
