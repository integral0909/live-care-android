package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Toast;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.ValidationsUtils;
import app.client.munchbear.munchbearclient.viewmodel.SignUpActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Roman on 7/7/2018.
 */
public class SignUpActivity extends BaseActivity{

    @BindView(R.id.editName) TextInputLayout editName;
    @BindView(R.id.editEmail) TextInputLayout editEmail;
    @BindView(R.id.editPhone) TextInputLayout editPhone;
    @BindView(R.id.editPassword) TextInputLayout editPassword;
    @BindView(R.id.editRepeatPassword) TextInputLayout editRepeatPassword;

    private SignUpActivityViewModel signUpActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signUpActivityViewModel = ViewModelProviders.of(this).get(SignUpActivityViewModel.class);

        initObservers();
    }

    private void initObservers() {
        signUpActivityViewModel.getSignUpResponseLiveData().observe(this, isUserSignUp -> {
            if (isUserSignUp) {
                openActivityWithFinish(MainActivity.class, true);
            }
        });

        signUpActivityViewModel.getErrorSignUpResponseLiveData().observe(this, error -> {
            if (error) {
                Toast.makeText(this, getString(R.string.toast_email_has_already_taken), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_sign_up_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.closeBtn)
    public void clickCloseBtn() {
        onBackPressed();
    }

    @OnClick(R.id.rootLayout)
    public void clickRootLayout(View view) {
        hideKeyboardFrom(this, view);
    }

    @OnClick(R.id.btnLoginIn)
    public void clickLoginIn() {
        openActivityWithFinish(LoginMainActivity.class, false);
    }

    @OnClick(R.id.signUpBtn)
    public void clickSignUp() {
        if (NetworkUtils.isConnectionAvailable(this)) {
            if (editName.getEditText() != null && editEmail.getEditText() != null && editPhone.getEditText() != null
                    && editPassword.getEditText() != null && editRepeatPassword.getEditText() != null) {
                String name = editName.getEditText().getText().toString().trim();
                String email = editEmail.getEditText().getText().toString().trim();
                String phone = editPhone.getEditText().getText().toString().trim();
                String password = editPassword.getEditText().getText().toString().trim();
                String repeatPassword = editRepeatPassword.getEditText().getText().toString().trim();
                if (validateFields(name, email, phone, password, repeatPassword)) {
                    signUpActivityViewModel.handleSignUpClick(name, email, phone, password);
                }
            }
        }
    }

    private boolean validateFields(String name, String email, String phone, String password, String repeatPassword) {
        clearErrors();

        return ValidationsUtils.isTextLengthValid(editName, name, getString(R.string.name_empty), getString(R.string.name_not_valid), 3) &&
                ValidationsUtils.isValidPattern(editEmail, email, ValidationsUtils.emailPatter, getString(R.string.email_empty), getString(R.string.email_not_valid))  &&
                ValidationsUtils.isValidPattern(editPhone, phone, ValidationsUtils.phonePatter, getString(R.string.phone_empty), getString(R.string.phone_not_valid))  &&
                ValidationsUtils.isTextLengthValid(editPassword, password, getString(R.string.password_empty), getString(R.string.password_not_valid), 8) &&
                ValidationsUtils.isRepeatPasswordValid(editRepeatPassword, password, repeatPassword, getString(R.string.repeat_password_empty), getString(R.string.repeat_password_not_equals));
    }

    private void clearErrors() {
        editEmail.setError(null);
        editName.setError(null);
        editPhone.setError(null);
        editPassword.setError(null);
        editRepeatPassword.setError(null);
    }


}
