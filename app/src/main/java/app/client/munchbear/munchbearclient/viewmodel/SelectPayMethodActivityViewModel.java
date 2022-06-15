package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import app.client.munchbear.munchbearclient.model.payment.PaymentMethod;
import app.client.munchbear.munchbearclient.model.response.PaymentMethodResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SelectPayMethodActivityViewModel extends CardListFragmentViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    MutableLiveData<PaymentMethod> paymentMethodLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> paymentMethodErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void getPaymentMethod() {
        ApiClient.getRepository().getPaymentMethod(CorePreferencesImpl.getCorePreferences().getSelectedRestaurantId(),
                CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<PaymentMethodResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<PaymentMethodResponse> paymentMethodResponseResponse) {
                        if (paymentMethodResponseResponse.isSuccessful()) {
                            paymentMethodLiveData.setValue(paymentMethodResponseResponse.body().getPaymentMethod());
                        } else {
                            paymentMethodErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        paymentMethodErrorLiveData.setValue(false);
                    }
                });
    }

    public void refreshCardList() {
        getCardList();
    }

    public MutableLiveData<PaymentMethod> getPaymentMethodLiveData() {
        return paymentMethodLiveData;
    }

    public MutableLiveData<Boolean> getPaymentMethodErrorLiveData() {
        return paymentMethodErrorLiveData;
    }
}
