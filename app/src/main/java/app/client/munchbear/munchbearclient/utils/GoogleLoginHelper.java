package app.client.munchbear.munchbearclient.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * @author Roman H.
 */

public class GoogleLoginHelper {

    private static GoogleLoginHelper googleLoginHelperInstance;

    private static GoogleSignInClient googleSignInClient;

    private GoogleLoginHelper() {

    }

    public static GoogleLoginHelper getInstance() {
        if (googleLoginHelperInstance == null) {
            googleLoginHelperInstance = new GoogleLoginHelper();
        }
        return googleLoginHelperInstance;
    }

    public static void setupGoogleLogin(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public static void logout() {
        googleSignInClient.signOut();
    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

}
