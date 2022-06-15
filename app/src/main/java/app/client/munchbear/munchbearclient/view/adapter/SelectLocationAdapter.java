package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectLocationAdapter extends RecyclerView.Adapter<SelectLocationAdapter.SelectLocationViewHolder> {

    private Context mContext;
    private OnRestaurantClick onRestaurantClickListener;
    private List<Restaurant> restaurantList;

    public SelectLocationAdapter(Context context, List<Restaurant> restaurants,  OnRestaurantClick clickListener) {
        this.mContext = context;
        this.restaurantList = restaurants;
        this.onRestaurantClickListener = clickListener;
    }

    @Override
    public SelectLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_location_item, parent, false);

        return new SelectLocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectLocationViewHolder holder, int position) {
        holder.bindData(restaurantList.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public interface OnRestaurantClick {
        void onItemRestaurantClick(int position);
    }

    public class SelectLocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.restaurantTitle) TextView restaurantTitle;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.workingStatus) TextView workingStatus;
        @BindView(R.id.workTimeCloseTime) TextView workTimeCloseTime;

        private Resources resources;
        private Restaurant restaurant;

        public SelectLocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            resources = mContext.getResources();
        }

        public void bindData(Restaurant restaurant) {
            this.restaurant = restaurant;

            restaurantTitle.setText(restaurant.getName());
            address.setText(restaurant.getAddress().getFullAddress());
            setupWorkingHours();
        }

        private void setupWorkingHours() {
            workingStatus.setText(restaurant.getWorkingHours().isOpen() ? resources.getString(R.string.select_location_open) :
                    resources.getString(R.string.select_location_close));
            workingStatus.setTextColor(restaurant.getWorkingHours().isOpen() ? resources.getColor(R.color.bottomMenuTextGreen) :
                    resources.getColor(R.color.badgeRed));

            String closeIn = String.format(resources.getString(R.string.select_location_closes_at),
                    DateUtils.convert24toUSAFormat(restaurant.getWorkingHours().getTo()));
            String openIn = String.format(resources.getString(R.string.select_location_open_at),
                    DateUtils.convert24toUSAFormat(restaurant.getWorkingHours().getFrom()));

            workTimeCloseTime.setText(restaurant.getWorkingHours().isOpen() ? closeIn : openIn);
        }

        @OnClick(R.id.rootLayout)
        public void clickRootLayout() {
            onRestaurantClickListener.onItemRestaurantClick(this.getLayoutPosition());
        }

    }
}
