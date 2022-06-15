package app.client.munchbear.munchbearclient.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Roman H.
 */

public class ViewUtils {

    public static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

}
