package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import app.client.munchbear.munchbearclient.R;

/**
 * Created by Roman on 7/5/2018.
 */
public class NetworkUtils {

    public static final int UNPROCESSABLE_ENTITY_ERROR_CODE = 422;

    public static boolean isConnectionAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnectionAvailable = cm.getActiveNetworkInfo() != null;
        if (!isConnectionAvailable) {
            showNoConnectionError(context);
        }
        return isConnectionAvailable;
    }

    private static void showNoConnectionError(Context context) {
        if (context == null) {
            return;
        }

        Toast.makeText(context, context.getString(R.string.network_utils_check_internet), Toast.LENGTH_SHORT).show();
    }

}
