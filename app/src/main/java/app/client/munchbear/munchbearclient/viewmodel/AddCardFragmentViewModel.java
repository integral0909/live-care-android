package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.request.AddCreditCardRequest;
import app.client.munchbear.munchbearclient.model.response.AddCreditCardResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class AddCardFragmentViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<CreditCard> addCreditCardLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> addCreditCardErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> updateCreditCardLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void showLoading(boolean showLoading) {
        isLoading.setValue(showLoading);
    }

    public void addCard(AddCreditCardRequest creditCardRequest) {
        ApiClient.getRepository().addCreditCard(creditCardRequest, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AddCreditCardResponse>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        compositeDisposable.add(disposable);
                    }

                    @Override
                    public void onSuccess(Response<AddCreditCardResponse> addCreditCardResponseResponse) {
                        if (addCreditCardResponseResponse.isSuccessful()) {
                            if (addCreditCardResponseResponse.body() != null) {
                                showLoading(false);
                                addCreditCardLiveData.setValue(addCreditCardResponseResponse.body().getCreditCard());
                            }
                        } else {
                            showLoading(false);
                            addCreditCardErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showLoading(false);
                        addCreditCardErrorLiveData.setValue(true);
                    }
                });
    }

    public void updateCard(AddCreditCardRequest creditCardRequest, int updateCardId) {
        ApiClient.getRepository().updateCard(creditCardRequest, CorePreferencesImpl.getCorePreferences().getAccessToken(), updateCardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        compositeDisposable.add(disposable);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        if (responseBodyResponse.isSuccessful()) {
                            updateCreditCardLiveData.setValue(true);
                        } else {
                            updateCreditCardLiveData.setValue(false);
                        }
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showLoading(false);
                        updateCreditCardLiveData.setValue(false);
                    }
                });
    }

    public MutableLiveData<CreditCard> getAddCreditCardLiveData() {
        return addCreditCardLiveData;
    }

    public MutableLiveData<Boolean> getAddCreditCardErrorLiveData() {
        return addCreditCardErrorLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getUpdateCreditCardLiveData() {
        return updateCreditCardLiveData;
    }
}
