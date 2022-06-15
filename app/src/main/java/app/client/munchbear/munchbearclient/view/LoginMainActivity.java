package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.ValidationsUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.LoginMainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginMainActivity extends SocLoginActivity {

    @BindView(R.id.editEmail) TextInputLayout emailET;
    @BindView(R.id.editPassword) TextInputLayout passwordET;
    @BindView(R.id.progressBarLayout) RelativeLayout progressBar;

    private LoginMainActivityViewModel loginMainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        ButterKnife.bind(this);

        loginMainActivityViewModel = ViewModelProviders.of(this).get(LoginMainActivityViewModel.class);

        initObservers();
        setupLoginViaFacebook();

    }

    private void initObservers() {
        loginMainActivityViewModel.getLoginResponseLiveData().observe(this, isLoginSuccess -> {
            progressBar.setVisibility(View.GONE);
            if (isLoginSuccess && !CorePreferencesImpl.getCorePreferences().firstStart()) {
                openActivityWithFinish(MainActivity.class, true);
            }
        });

        loginMainActivityViewModel.getErrorLoginResponseLiveData().observe(this, error -> {
            if (error) {
                Toast.makeText(this, getString(R.string.toast_not_valid_data), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.closeBtn)
    public void clickCloseBtn() {
        onBackPressed();
    }

    @OnClick(R.id.btnRecover)
    public void clickRecoverPassword() {
        //TODO Add recovery logic
    }

    @OnClick(R.id.rootLayout)
    public void clickRootLayout(View view) {
        hideKeyboardFrom(this, view);
    }

    @OnClick(R.id.loginBtn)
    public void clickLogin() {
        if (NetworkUtils.isConnectionAvailable(this)) {
            if (emailET.getEditText() != null && passwordET.getEditText() != null) {
                String email = emailET.getEditText().getText().toString().trim();
                String password = passwordET.getEditText().getText().toString().trim();
                if (validateFields(email, password)) {
                    handleLoginClick(email, password);
                }
            }
        }
    }

    private boolean validateFields(String email, String password) {
        emailET.setError(null);
        passwordET.setError(null);

        return ValidationsUtils.isValidPattern(emailET, email, ValidationsUtils.emailPatter, getString(R.string.email_empty), getString(R.string.email_not_valid))  &&
                ValidationsUtils.isTextLengthValid(passwordET, password, getString(R.string.password_empty), getString(R.string.password_not_valid), 8);
    }

    private void handleLoginClick(String userName, String password) {
        if (NetworkUtils.isConnectionAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            loginMainActivityViewModel.handleLoginClick(userName, password);
        }
    }

    @OnClick(R.id.btnFacebook)
    public void clickLoginViaFacebook() {
        if(NetworkUtils.isConnectionAvailable(this)) {
            clickFacebookLogin();
        }
    }

    @OnClick(R.id.btnGoogle)
    public void clickLoginViaGoogle() {
        if(NetworkUtils.isConnectionAvailable(this)) {
            clickGoogleLogin();
        }
    }

    @OnClick(R.id.btnSignUp)
    public void clickSignUp() {
        openActivityWithFinish(SignUpActivity.class, false);
    }

    @Override
    protected void showProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
