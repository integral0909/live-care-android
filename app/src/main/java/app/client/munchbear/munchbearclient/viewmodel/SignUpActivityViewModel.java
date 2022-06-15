package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.request.RegisterRequestModel;
import app.client.munchbear.munchbearclient.model.response.LoginResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SignUpActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> signUpResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> errorSignUpResponseLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void handleSignUpClick(String name, String email, String phone, String password) {
        RegisterRequestModel registerRequestModel = new RegisterRequestModel(name, email, phone, password);
        ApiClient.getRepository().register(registerRequestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<LoginResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<LoginResponse> signUpResponse) {
                        if (signUpResponse.isSuccessful()) {
                            LoginResponse response = signUpResponse.body();
                            CorePreferencesImpl.setLoginDataInPreferences(response.getToken(), response.getUser(),
                                    LoginType.EMAIL);
                            signUpResponseLiveData.setValue(true);
                        } else {
                            errorSignUpResponseLiveData.setValue(handleErrorType(signUpResponse.code()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorSignUpResponseLiveData.setValue(false);
                    }
                });
    }

    private boolean handleErrorType(int errorCode) {
        if (errorCode == NetworkUtils.UNPROCESSABLE_ENTITY_ERROR_CODE) {
            return true;
        } else {
            return false;
        }
    }

    public MutableLiveData<Boolean> getSignUpResponseLiveData() {
        return signUpResponseLiveData;
    }

    public MutableLiveData<Boolean> getErrorSignUpResponseLiveData() {
        return errorSignUpResponseLiveData;
    }
}
