package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

import java.text.DecimalFormat;

import app.client.munchbear.munchbearclient.R;

public class TextUtils {

    public static void setupTotalPrice(Context context, TextView totalDollar, TextView totalPoint,
               double costDollar, int costPoint, int count) {
        DecimalFormat decimFormat = new DecimalFormat("0.00");
        Resources resources = context.getResources();

        totalDollar.setText(String.format(resources.getString(R.string.detail_dish_price_format),
                decimFormat.format(costDollar * count)));

        if (LoyaltyProgram.isLoyaltyEnable()) {
            totalPoint.setText(String.valueOf(costPoint * count));
        }
    }

}
