package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.core.Core;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.LoadActivityViewModel;

/**
 * Created by Roman on 7/5/2018.
 */
public class LoadActivity extends BaseActivity{

    private LoadActivityViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(LoadActivityViewModel.class);

        if (CorePreferencesImpl.getCorePreferences().firstStart()) {
            openActivityWithFinish(LoginStarterActivity.class, false);
            CorePreferencesImpl.getCorePreferences().setUserHasAvatar(false);
        } else {
            openActivityWithFinish(MainActivity.class, false);
        }

        CorePreferencesImpl.getCorePreferences().setForceOpenMyOrder(false);
        CorePreferencesImpl.getCorePreferences().setForceOpenMenu(false);
    }

}
