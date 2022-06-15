package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CartChangeListener;
import app.client.munchbear.munchbearclient.utils.GoogleLoginHelper;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.SocLoginViewModel;

/**
 * @author Roman H.
 */

public class SocLoginActivity extends BaseActivity {

    protected CallbackManager facebookCallbackManager;
    private SocLoginViewModel socLoginViewModel;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socLoginViewModel = ViewModelProviders.of(this).get(SocLoginViewModel.class);
        facebookCallbackManager = CallbackManager.Factory.create();

        initObservers();
    }

    private void initObservers() {
        socLoginViewModel.getSocNetworkLoginLiveData().observe(this, login -> {
            showProgressBar(false);
            if (login && !CorePreferencesImpl.getCorePreferences().firstStart()) {
                openActivityWithFinish(MainActivity.class, true);
            }
        });

        socLoginViewModel.getSocNetworkLoginErrorLiveData().observe(this, error -> {
            if (error) {
                Toast.makeText(this, getString(R.string.toast_login_google_error), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_login_facebook_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void setupLoginViaFacebook() {
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook login:", "SUCCESS");
                showProgressBar(true);
                getFacebookUserData(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Facebook login:", "CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook login:", "ERROR");
            }
        });

    }

    private void getFacebookUserData(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                String id = object.getString("id");
                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                //TODO If need save user image_url

                socLoginViewModel.loginWithFacebookToken(accessToken.getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    protected void clickFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    protected void clickGoogleLogin() {
        GoogleLoginHelper.setupGoogleLogin(this);
        Intent gSignIntent = GoogleLoginHelper.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(gSignIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            new RetrieveTokenTask().execute(account.getEmail());

        } catch (ApiException e) {
            Log.w("Google Sign error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e("TAG", e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), 12);
            } catch (GoogleAuthException e) {
                Log.e("TAG", e.getMessage());
            }
            Log.e("TAG", token);
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.isEmpty()) {
                showProgressBar(true);
                socLoginViewModel.loginWithGoogleToken(s);
            }
        }
    }

    protected void showProgressBar(boolean state) {
    }
}
