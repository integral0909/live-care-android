package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.squareup.picasso.Picasso;

import app.client.munchbear.munchbearclient.R;

public class ImageUtils {

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void setImageFromUrl(String imgUrl, ImageView imageView, int defaultImg) {
        if (imageView != null) {
            Picasso.get()
                    .load(imgUrl)
                    .placeholder(defaultImg)
                    .error(defaultImg)
                    .into(imageView);
        }
    }

    public static void setImageFromUrl(String imgUrl, ImageView imageView) {
        if (imageView != null) {
            Picasso.get()
                    .load(imgUrl)
                    .into(imageView);
        }
    }

    public static int getDpFromPx(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }
}
