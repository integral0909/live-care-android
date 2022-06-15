package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Roman on 7/18/2018.
 */
public class ChangeProfileActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> saveUserDataLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void saveUserData(UserData userData, String name, String email, String phone) {
        if (userData == null) {
            userData = new UserData();
        }
        userData.setName(name);
        userData.setEmail(email);
        userData.setPhone(phone);

        CorePreferencesImpl.getCorePreferences().setUserData(userData);

        saveUserDataLiveData.setValue(true);
    }

    public MutableLiveData<Boolean> getSaveUserDataLiveData() {
        return saveUserDataLiveData;
    }

}
