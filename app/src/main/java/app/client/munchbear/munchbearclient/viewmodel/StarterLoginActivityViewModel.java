package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.request.LoginBaseRequestModel;
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
 * Created by Roman on 7/5/2018.
 */
public class StarterLoginActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Login how as guest
    MutableLiveData<Boolean> loginAsGuestLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> loginAsGuestErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void loginAsGuest() {
        ApiClient.getRepository().guestLogin(new LoginBaseRequestModel())
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
                                    LoginType.GUEST);
                            loginAsGuestLiveData.setValue(true);
                        } else {
                            loginAsGuestErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loginAsGuestErrorLiveData.setValue(true);
                    }
                });
    }

    public MutableLiveData<Boolean> getLoginAsGuestLiveData() {
        return loginAsGuestLiveData;
    }

    public MutableLiveData<Boolean> getLoginAsGuestErrorLiveData() {
        return loginAsGuestErrorLiveData;
    }
}
