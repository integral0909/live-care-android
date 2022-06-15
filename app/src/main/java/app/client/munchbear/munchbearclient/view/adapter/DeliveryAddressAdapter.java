package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.DeliveryAddressViewHolder> {

    private Context context;
    private Resources resources;
    private int loginType;
    private List<Address> addressList;
    private ChangeAddressListListener changeAddressListListener;
    private boolean fromProfile;

    public DeliveryAddressAdapter(Context context, List<Address> delAddList, ChangeAddressListListener listener, boolean fromProfile) {
        this.context = context;
        this.addressList = delAddList;
        this.changeAddressListListener = listener;
        this.fromProfile = fromProfile;
    }

    @Override
    public DeliveryAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_address_item, parent, false);

        loginType = CorePreferencesImpl.getCorePreferences().getLoginType();
        resources = context.getResources();

        return new DeliveryAddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeliveryAddressViewHolder holder, int position) {
        holder.bindData(addressList.get(position));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }


    private void removeItemAt(int position) {
        addressList.remove(position);
        notifyDataSetChanged();
        checkEmptyList();
    }

    public interface ChangeAddressListListener {
        void hideListTitle();
        void finishFragment();
    }

    private void checkEmptyList() {
        if (addressList.size() == 0) {
            changeAddressListListener.hideListTitle();
        }
    }

    private void moveToTopPosition(int position) {
        Address selectAddress = addressList.get(position);
        addressList.remove(position);
        addressList.add(0, selectAddress);
        notifyDataSetChanged();
    }

    public class DeliveryAddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.map) ImageView mapImg;
        @BindView(R.id.mapMarker) ImageView mapMarker;
        @BindView(R.id.dotsMore) ImageView dotsMore;
        @BindView(R.id.streetAndHouse) TextView streetAndHouse;
        @BindView(R.id.cityAndCountry) TextView cityAndCountry;
        @BindView(R.id.postIndex) TextView postIndex;

        public DeliveryAddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Address address) {
            setupAddressMapView(address);
            String city = (TextUtils.isEmpty(address.getCity()) ? "" : address.getCity());
            String country = (TextUtils.isEmpty(address.getCountryName()) ? "" : address.getCountryName());
            cityAndCountry.setVisibility(TextUtils.isEmpty(city) && TextUtils.isEmpty(country) ? View.GONE : View.VISIBLE);
            streetAndHouse.setText(String.format(resources.getString(R.string.delivery_address_street_and_house), address.getStreet(), address.getHouse()));
            cityAndCountry.setText(TextUtils.isEmpty(city) ? country : String.format(resources.getString(R.string.delivery_address_city_and_country), city, country));
            postIndex.setText(address.getPostalCode());
        }

        private void setupAddressMapView(Address address) {
            CustomMarkerHelper.setLocationPin(mapMarker, DeliveryVariant.TYPE_DELIVERY, context);
            GoogleMapHelper.setStaticMap(context, address.getLat(), address.getLng(), mapImg, true);
        }

        @OnClick(R.id.rootLayout)
        public void onDeliveryAddressClick(View view) {
            if (fromProfile) {
                PopupMenu popupMenu = new PopupMenu(context, cityAndCountry);
                popupMenu.getMenuInflater().inflate(R.menu.delivery_address, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.makeDefault:
                            moveToTopPosition(getAdapterPosition());
                            break;
                        case R.id.delete:
                            removeItemAt(getAdapterPosition());
                            break;
                    }
                    CorePreferencesImpl.getCorePreferences().setPreviousDeliveryAddressList(addressList);
                    return true;
                });

                popupMenu.show();
            } else {
                moveToTopPosition(getAdapterPosition());
                changeAddressListListener.finishFragment();
            }
        }
    }
}
