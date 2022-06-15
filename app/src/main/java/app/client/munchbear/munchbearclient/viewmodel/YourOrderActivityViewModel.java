package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.common.api.Api;

import app.client.munchbear.munchbearclient.model.order.OrderDetails;
import app.client.munchbear.munchbearclient.model.response.OrderDetailsResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class YourOrderActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<OrderDetails> orderDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> orderDetailsErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void getOrderDetailsById(String orderId) {
        ApiClient.getRepository().getOrderDetails(orderId, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<OrderDetailsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OrderDetailsResponse> orderDetailsResponseResponse) {
                        if (orderDetailsResponseResponse.isSuccessful() && orderDetailsResponseResponse.body() != null) {
                            orderDetailsLiveData.setValue(orderDetailsResponseResponse.body().getOrderDetails());
                        } else {
                            orderDetailsErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        orderDetailsErrorLiveData.setValue(false);
                    }
                });
    }

    public MutableLiveData<OrderDetails> getOrderDetailsLiveData() {
        return orderDetailsLiveData;
    }

    public MutableLiveData<Boolean> getOrderDetailsErrorLiveData() {
        return orderDetailsErrorLiveData;
    }
}
