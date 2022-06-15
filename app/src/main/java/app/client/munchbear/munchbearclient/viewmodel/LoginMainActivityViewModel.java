package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.request.LoginRequestModel;
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
 * Created by Roman on 7/7/2018.
 */
public class LoginMainActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> loginResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> errorLoginResponseLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void handleLoginClick(String email, String password) {
        LoginRequestModel loginRequestModel = new LoginRequestModel(email, password);
        ApiClient.getRepository().login(loginRequestModel)
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
                            LoginResponse response = loginResponseResponse.body();
                            CorePreferencesImpl.setLoginDataInPreferences(response.getToken(), response.getUser(),
                                    LoginType.EMAIL);
                            loginResponseLiveData.setValue(true);
                        } else {
                            errorLoginResponseLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorLoginResponseLiveData.setValue(true);
                    }
                });
    }

    public MutableLiveData<Boolean> getLoginResponseLiveData() {
        return loginResponseLiveData;
    }

    public MutableLiveData<Boolean> getErrorLoginResponseLiveData() {
        return errorLoginResponseLiveData;
    }
}
