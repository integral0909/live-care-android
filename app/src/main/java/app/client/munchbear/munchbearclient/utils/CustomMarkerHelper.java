package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.restaurant.DeliveryZone;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

public class CustomMarkerHelper {

    private static final double ADDITIONAL_LATITUDE_FOR_USER_MARKER = 0.000600;

    private static Bitmap loadBitmapFromView(View v) {
        RelativeLayout view = new RelativeLayout(v.getContext());
        LayoutInflater.from(v.getContext()).inflate(R.layout.photo_map_marker, view, true);

        ImageView avatar = (ImageView) view.findViewById(R.id.mapAvatar);
        if (CorePreferencesImpl.getCorePreferences().getLoginType() == LoginType.GUEST) {
            avatar.setImageResource(R.mipmap.map_avatar_guest);
        } else {
            avatar.setImageBitmap(UserAvatar.getUserAvatar(v.getContext()));
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);
        return bitmap;
    }

    /**
     * Add 0.000600 to v coordinate because not showing full custom pin
     */
    private static void focusToUserMarker(LatLng latLng, GoogleMap googleMap, boolean focus) {
        if (focus) {
            LatLng myLatLng2 = new LatLng(latLng.latitude + ADDITIONAL_LATITUDE_FOR_USER_MARKER, latLng.longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng2, 15f));
        }
    }

    public static void focusToAddress(Address address, GoogleMap googleMap) {
        if (address != null) {
            LatLng myLatLng = new LatLng(address.getLat(), address.getLng());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f));
        }
    }

    public static void addCustomUserMarker(GoogleMap googleMap, LatLng ltLg, Context context, boolean focusCamera) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.photo_map_marker, null);

        googleMap.addMarker(new MarkerOptions()
                .position(ltLg)
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarkerHelper.loadBitmapFromView(view))));

        focusToUserMarker(ltLg, googleMap, focusCamera);

    }

    public static void addRestaurantsMarkers(GoogleMap googleMap, List<Address> addressList) {
        for (Address address : addressList) {
            LatLng mLatLng = new LatLng(address.getLat(), address.getLng());
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_for_map_mini))
                    .position(mLatLng));
        }
    }

    public static void addDeliveryRadiusMarkers(GoogleMap googleMap, List<DeliveryZone> deliveryZoneList, Context context) {
        for (DeliveryZone deliveryZone : deliveryZoneList) {
            LatLng mLatLng = new LatLng(deliveryZone.getLat(), deliveryZone.getLng());
            int zoneRadius = deliveryZone.getRadius();

            googleMap.addCircle(new CircleOptions()
                    .center(mLatLng)
                    .radius(zoneRadius)
                    .strokeWidth(6)
                    .strokeColor(context.getResources().getColor(R.color.colorPrimary))
                    .fillColor(context.getResources().getColor(R.color.greenZoneInside))
                    .clickable(true));
        }
    }

    public static void showDeliveryTypeMarker(int type, GoogleMap googleMap, Address address, Context context) {
        if (type == DeliveryVariant.TYPE_DELIVERY) {
            LatLng latLng = new LatLng(address.getLat(), address.getLng());
            addCustomUserMarker(googleMap, latLng, context, true);
        } else {
            List<Address> addressList = new ArrayList<>();
            addressList.add(address);
            addRestaurantsMarkers(googleMap, addressList);
            focusToAddress(address, googleMap);
        }
    }

    public static void setLocationPin(ImageView pinView, int deliveryType, Context context) {
        switch (deliveryType) {
            case DeliveryVariant.TYPE_DELIVERY:
                setUserMarker(pinView, context);
                break;
            case DeliveryVariant.TYPE_EAT_IN:
                pinView.setImageResource(R.mipmap.marker_for_map_mini);
                break;
            case DeliveryVariant.TYPE_PICK_UP:
                pinView.setImageResource(R.mipmap.marker_for_map_mini);
                break;
            case DeliveryVariant.TYPE_TABLE_RESERVATION:
                pinView.setImageResource(R.mipmap.marker_for_map_mini);
                break;
        }
    }

    private static void setUserMarker(ImageView imageView, Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.photo_map_marker, null);
        imageView.setImageBitmap(CustomMarkerHelper.loadBitmapFromView(view));
    }

}
