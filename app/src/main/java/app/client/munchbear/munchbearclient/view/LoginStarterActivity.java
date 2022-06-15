package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Arrays;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.StarterLoginActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Roman on 7/5/2018.
 */
public class LoginStarterActivity extends SocLoginActivity {

    @BindView(R.id.progressBarLayout) RelativeLayout progressBar;

    private StarterLoginActivityViewModel starterLoginActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_starter);
        ButterKnife.bind(this);

        starterLoginActivityViewModel = ViewModelProviders.of(this).get(StarterLoginActivityViewModel.class);
        initObservers();
        setupLoginViaFacebook();
    }

    private void initObservers() {
        starterLoginActivityViewModel.getLoginAsGuestLiveData().observe(this, loginAsGuest -> {
            if (loginAsGuest && !CorePreferencesImpl.getCorePreferences().firstStart()) {
                progressBar.setVisibility(View.GONE);
                openActivityWithFinish(MainActivity.class, false);
            }
        });

        starterLoginActivityViewModel.getLoginAsGuestErrorLiveData().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.somethings_went_wrong), Toast.LENGTH_SHORT).show();
        });
    }

    @OnClick(R.id.btnLogin)
    public void clickLogin() {
        openActivity(LoginMainActivity.class, null);
    }

    @OnClick(R.id.btnGuest)
    public void clickGuest() {
        if (NetworkUtils.isConnectionAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            starterLoginActivityViewModel.loginAsGuest();
        }
    }

    @OnClick(R.id.btnCreateAccount)
    public void clickCreateAccount() {
        openActivity(SignUpActivity.class, null);
    }

    @OnClick(R.id.btnGoogle)
    public void clickLoginViaGoogle() {
        if(NetworkUtils.isConnectionAvailable(this)) {
            clickGoogleLogin();
        }
    }

    @OnClick(R.id.btnFacebook)
    public void clickLoginViaFacebook() {
        if(NetworkUtils.isConnectionAvailable(this)) {
            clickFacebookLogin();
        }
    }

    @Override
    protected void showProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
