package app.client.munchbear.munchbearclient.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;
import java.util.List;
import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.utils.CustomMarkerHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.view.fragment.BaseFragment.INTENT_BUNDLE;

/**
 * @author Roman H.
 */

public class DetailsMapActivity extends UserLocationBaseActivity implements OnMapReadyCallback {

    public static final String KEY_ADDRESS = "key.address";
    public static final String KEY_DELIVERY_TYPE = "key.delivery.type";

    @BindView(R.id.toolbarTitle) TextView toolbarTitle;

    private Address address;
    private int deliveryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_map);
        ButterKnife.bind(this);

        getIntentData();
        initGoogleMap();
    }

    private void getIntentData() {
        if (getIntent().hasExtra(INTENT_BUNDLE)) {
            Bundle bundle = getIntent().getBundleExtra(INTENT_BUNDLE);
            address = bundle.getParcelable(KEY_ADDRESS);
            deliveryType = bundle.getInt(KEY_DELIVERY_TYPE);
        }
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupCoordinates() {
        if (address != null) {
            CustomMarkerHelper.showDeliveryTypeMarker(deliveryType, googleMap, address, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        setupCoordinates();
    }

    @OnClick(R.id.buildRouteBtn)
    public void clickBuildRoute() {
        buildRouteInMapApp();
    }

    private void buildRouteInMapApp() {
        Uri mapUri = Uri.parse("http://maps.google.com/maps?daddr=" + String.valueOf(address.getLat()) + "," + String.valueOf(address.getLng()));
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }
}
