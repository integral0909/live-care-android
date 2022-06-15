package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.request.FindDeliveryZoneRequest;
import app.client.munchbear.munchbearclient.model.response.FindDeliveryZoneResponse;
import app.client.munchbear.munchbearclient.model.restaurant.DeliveryZone;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SelectLocationActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<DeliveryZone> deliveryZoneLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> deliveryZoneErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void findDeliveryZone(int restaurantId) {
        Address currentDeliveryAddress = Cart.getInstance().getSelectedDeliveryAddress();
        ApiClient.getRepository().findDeliveryZone(restaurantId, CorePreferencesImpl.getCorePreferences().getAccessToken(),
                new FindDeliveryZoneRequest(currentDeliveryAddress.getLat(), currentDeliveryAddress.getLng()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<FindDeliveryZoneResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<FindDeliveryZoneResponse> findDeliveryZoneResponseResponse) {
                        if (findDeliveryZoneResponseResponse.isSuccessful()) {
                            if (findDeliveryZoneResponseResponse.body() != null) {
                                deliveryZoneLiveData.setValue(findDeliveryZoneResponseResponse.body().getDeliveryZone());
                            }
                        } else {
                            deliveryZoneErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        deliveryZoneErrorLiveData.setValue(true);
                    }
                });
    }

    public MutableLiveData<DeliveryZone> getDeliveryZoneLiveData() {
        return deliveryZoneLiveData;
    }

    public MutableLiveData<Boolean> getDeliveryZoneErrorLiveData() {
        return deliveryZoneErrorLiveData;
    }
}
