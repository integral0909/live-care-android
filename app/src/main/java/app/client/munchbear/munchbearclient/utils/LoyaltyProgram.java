package app.client.munchbear.munchbearclient.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

/**
 * @author Roman H.
 */

public class LoyaltyProgram {

    public static boolean isLoyaltyEnable() {
        return CorePreferencesImpl.getCorePreferences().isLoyaltyEnable();
    }

    public static void goneViewIfLoyaltyDisable(View view) {
        view.setVisibility(isLoyaltyEnable() ? View.VISIBLE : View.GONE);
    }

    public static void invisibleViewIfLoyaltyDisable(View view) {
        view.setVisibility(isLoyaltyEnable() ? View.VISIBLE : View.INVISIBLE);
    }

    public static void showViewForGuest(View view, boolean isGuest) {
        if (isGuest) {
            view.setVisibility(View.GONE);
        } else {
            LoyaltyProgram.goneViewIfLoyaltyDisable(view);
        }
    }

    public static void showProfileHeader(View view, boolean isGuest) {
        if (isGuest) {
            goneViewIfLoyaltyDisable(view);
        }
    }

    public static void setupCartViews(TextView orTxt, ImageView imageStar, TextView cartTotalPoint, TextView cartTotalDollar) {
        LoyaltyProgram.goneViewIfLoyaltyDisable(orTxt);
        LoyaltyProgram.goneViewIfLoyaltyDisable(imageStar);
        LoyaltyProgram.invisibleViewIfLoyaltyDisable(cartTotalPoint);
        if (isLoyaltyEnable()) {
            ViewUtils.setMargins(cartTotalDollar, 0, 0, 0, 0);
        }
    }
}
