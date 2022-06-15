package app.client.munchbear.munchbearclient;

import android.app.Application;

import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.FacebookSdk;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Roman on 7/5/2018.
 */
public class FoodClientApplication extends Application {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        CorePreferencesImpl.getCorePreferences().init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
