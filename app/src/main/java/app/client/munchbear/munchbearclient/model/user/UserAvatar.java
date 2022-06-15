package app.client.munchbear.munchbearclient.model.user;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Class controller of User avatar
 */
public class UserAvatar {

    /**
     * Save user avatar to path /data/data/app_package/app_data/imageDir/user_avatar.jpg using
     * compressing to 100 quality
     */
    public static void setUserAvatar(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "user_avatar.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            CorePreferencesImpl.getCorePreferences().setUserHasAvatar(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getUserAvatar(Context context) {
        if (CorePreferencesImpl.getCorePreferences().getLoginType() == LoginType.GUEST) {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.map_avatar_guest);
        } else {
            if (CorePreferencesImpl.getCorePreferences().hasUserAvatar()) {
                if (getLocalSavedUserAvatar() != null) {
                    return getLocalSavedUserAvatar();
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.map_avatar_guest);
                }
            } else {
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.map_avatar_guest);
            }
        }
    }

    /**
     * @return Bitmap from path /data/data/app_package/app_data/imageDir/user_avatar.jpg
     */
    private static Bitmap getLocalSavedUserAvatar() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File f = new File(directory, "user_avatar.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete user avatar image file from path /data/data/app_package/app_data/imageDir/user_avatar.jpg
     */
    public static void deleteUserAvatar() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File file = new File(directory, "user_avatar.jpg");
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            Log.e("App", "Exception while deleting file " + e.getMessage());
        }
    }

}
