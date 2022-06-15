package app.client.munchbear.munchbearclient.model.store;

import java.io.IOException;

import app.client.munchbear.munchbearclient.model.request.TokenRefreshModel;
import app.client.munchbear.munchbearclient.model.response.LoginResponse;
import app.client.munchbear.munchbearclient.model.user.Token;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.annotations.Nullable;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Response;

/*
 * @author Nazar V.
 */
public class Authenticator implements okhttp3.Authenticator {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Nullable
    @Override
    public Request authenticate(Route route, okhttp3.Response response) throws IOException {
        synchronized (this) {
            String accessToken = CorePreferencesImpl.getCorePreferences().getToken();
            String incomingHeader = response.request().header(HEADER_AUTHORIZATION);

            /* if token is the same, then we should refresh it,
            else it was refreshed in another request so we can retry initial request */
            if (incomingHeader.equals(accessToken)) {
                return refreshToken(response);
            } else {
                return response.request().newBuilder().header(HEADER_AUTHORIZATION, accessToken).build();
            }
        }
    }

    private Request refreshToken(okhttp3.Response response) {

        TokenRefreshModel tokenRefreshModel = new TokenRefreshModel("");
        Response<LoginResponse> refreshResponse = ApiClient.getRepository().refreshToken(tokenRefreshModel).blockingGet();
        if (!refreshResponse.isSuccessful()) {
            /*We couldn't refresh, so send the unauthorized response to consumer*/
            return null;
        }

        LoginResponse loginBody = refreshResponse.body();
        String token = loginBody.getToken();
        //String accessToken = token.getAccessToken();
        setValuesInPreferences(token);

        return response.request().newBuilder().header(HEADER_AUTHORIZATION, token).build();
    }

    private void setValuesInPreferences(String token) {
        CorePreferencesImpl corePref = CorePreferencesImpl.getCorePreferences();
        corePref.setToken(token);
//        corePref.setAccessToken(accessToken);
//        corePref.setRefreshToken(refreshToken);
//        corePref.setExpiresToken(expiresIn);
    }
}


