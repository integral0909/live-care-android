package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.response.GetUserDataResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<UserData> userDataLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> userDataErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void getUserData() {
        ApiClient.getRepository().getUser(CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<GetUserDataResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<GetUserDataResponse> userDataResponse) {
                        if (userDataResponse.isSuccessful()) {
                            UserData userData = userDataResponse.body().getUserData();
                            handleUpdateUserData(userData);
                        } else {
                            userDataErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        userDataErrorLiveData.setValue(true);
                    }
                });
    }

    private void handleUpdateUserData(UserData userData) {
        if (userData != null) {
            CorePreferencesImpl.getCorePreferences().setUserData(userData);
            userDataLiveData.setValue(userData);
        }
    }

    public MutableLiveData<UserData> getUserDataLiveData() {
        return userDataLiveData;
    }

    public MutableLiveData<Boolean> getUserDataErrorLiveData() {
        return userDataErrorLiveData;
    }
}
