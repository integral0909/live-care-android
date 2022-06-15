package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.request.SocLoginRequestModel;
import app.client.munchbear.munchbearclient.model.response.LoginResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Roman H.
 */

public class SocLoginViewModel extends ViewModel{

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected MutableLiveData<Boolean> socNetworkLoginLiveData = new MutableLiveData<>();
    protected MutableLiveData<Boolean> socNetworkLoginErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void loginWithFacebookToken(String fbToken) {
        SocLoginRequestModel requestModel = new SocLoginRequestModel(fbToken, "");
        ApiClient.getRepository().facebookLogin(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<LoginResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<LoginResponse> loginResponseResponse) {
                        if (loginResponseResponse.isSuccessful()) {
                            handleLoginResponse(loginResponseResponse);
                            socNetworkLoginLiveData.setValue(true);
                        } else {
                            socNetworkLoginErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        socNetworkLoginErrorLiveData.setValue(true);
                    }
                });
    }

    public void loginWithGoogleToken(String gToken) {
        SocLoginRequestModel requestModel = new SocLoginRequestModel(gToken, "");
        ApiClient.getRepository().googleLogin(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<LoginResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<LoginResponse> loginResponseResponse) {
                        if (loginResponseResponse.isSuccessful()) {
                            handleLoginResponse(loginResponseResponse);
                            socNetworkLoginLiveData.setValue(true);
                        } else {
                            socNetworkLoginErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        socNetworkLoginErrorLiveData.setValue(false);
                    }
                });
    }

    private void handleLoginResponse(Response<LoginResponse> loginResponseResponse) {
        LoginResponse response = loginResponseResponse.body();
        CorePreferencesImpl.setLoginDataInPreferences(response.getToken(), response.getUser(),
                LoginType.EMAIL);
    }

    public MutableLiveData<Boolean> getSocNetworkLoginLiveData() {
        return socNetworkLoginLiveData;
    }

    public MutableLiveData<Boolean> getSocNetworkLoginErrorLiveData() {
        return socNetworkLoginErrorLiveData;
    }
}
