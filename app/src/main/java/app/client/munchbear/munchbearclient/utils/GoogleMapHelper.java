package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;

public class GoogleMapHelper {

    private static final String GOOGLE_STATIC_MAPS_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String GOOGLE_STATIC_MAPS_KEY = "AIzaSyBkTX0hi8ORcfQMhgY6toC2Jm9-4jzcmJg";
    private static final int GOOGLE_STATIC_MAPS_ZOOM = 17;
    private static final int GOOGLE_STATIC_MAPS_HEIGHT_BIG = 220;

    private static final int GOOGLE_STATIC_MAPS_WIDTH_SMALL = 260;
    private static final int GOOGLE_STATIC_MAPS_HEIGHT_SMALL = 180;

    // Get Address from latitude and longitude //
    public static Address getAddressDataList(Context ctx, double lat, double lng) {
        Address address;
        String postalCode = "";
        String street = "";
        String house = "";
        String countryCode = "";
        String countryName = "";
        String city = "";
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.ENGLISH); //TODO In future if will be need add another
            List<android.location.Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses.size() != 0) {
                postalCode = addresses.get(0).getPostalCode();
                street = addresses.get(0).getThoroughfare();
                house = addresses.get(0).getSubThoroughfare();
                countryCode = addresses.get(0).getCountryCode();
                countryName = addresses.get(0).getCountryName();
                city = addresses.get(0).getLocality();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        address = new Address(street, house, "", postalCode, city, countryCode, countryName, lat, lng);
        return address;
    }

    public static LatLng getLatLngFromLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }

    public static LatLng getLatLng(Context context, String fullAddress) {
        List<android.location.Address> addressList;
        LatLng userLatLng = null;

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);//TODO In future if will be need add another
        try {
            addressList = geocoder.getFromLocationName(fullAddress, 5);

            if (addressList == null) {
                return null;
            }

            if (addressList.size() == 0) {
                return null;
            }

            android.location.Address addr = addressList.get(0);
            userLatLng = new LatLng(addr.getLatitude(), addr.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userLatLng;
    }

    public static void setStaticMap(Context context, double lat, double lng, ImageView imgView, boolean smallType) {
        StringBuilder builder = new StringBuilder();
        builder.append(GOOGLE_STATIC_MAPS_BASE_URL);
        builder.append("center=");
        builder.append(lat + ",");
        builder.append(lng + "&");
        builder.append("zoom=" + GOOGLE_STATIC_MAPS_ZOOM + "&");
        builder.append(getStaticMapSize(context, smallType));
        builder.append("maptype=roadmap&");
        builder.append("scale=2&");
        builder.append("key=" + GOOGLE_STATIC_MAPS_KEY);

        ImageUtils.setImageFromUrl(builder.toString(), imgView);
    }

    private static String getStaticMapSize(Context context, boolean smallType) {
        if (smallType) {
            return "size=" + GOOGLE_STATIC_MAPS_WIDTH_SMALL + "x" + GOOGLE_STATIC_MAPS_HEIGHT_SMALL + "&";
        } else {
           return "size=" + getDisplayWidth(context) + "x" + GOOGLE_STATIC_MAPS_HEIGHT_BIG + "&";
        }
    }

    /**
     * @return display width x2 for more details from google map api
     */
    private static String getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int widthInt = (int) width;
        return String.valueOf(widthInt * 2);
    }

    public static Bitmap getLogoMockForMaps(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.maps_mock_layout, null);

        RelativeLayout view = new RelativeLayout(v.getContext());
        LayoutInflater.from(v.getContext()).inflate(R.layout.maps_mock_layout, view, true);

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
