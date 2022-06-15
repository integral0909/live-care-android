package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.model.order.make.MakeOrder;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.utils.OrderRequestBuilder;
import app.client.munchbear.munchbearclient.utils.ValidationsUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.DeliveryTimeActivity;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CheckoutActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> createOrderLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> createOrderErrorLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void createOrder(MakeOrder makeOrder, Context context) {
        if (ValidationsUtils.isMakeOrderDataValid(makeOrder, context)) {
            int restaurantId = CorePreferencesImpl.getCorePreferences().getSelectedRestaurantId();
            String accessToken = CorePreferencesImpl.getCorePreferences().getAccessToken();
            ApiClient.getRepository().createOrder(restaurantId, accessToken, makeOrder)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<ResponseBody>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                            if (responseBodyResponse.isSuccessful()) {
                                createOrderLiveData.setValue(true);
                            } else {
                                createOrderErrorLiveData.setValue(true);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            createOrderErrorLiveData.setValue(true);
                        }
                    });
        }
    }

    public void handleDeliveryTime(int deliveryTimeType, Intent data, TextView deliveryTimeDate, TextView deliveryTimeTime) {
        if (deliveryTimeType == DeliveryTimeActivity.TYPE_SPECIFIC_TIME) {
            String date = data.getStringExtra(DeliveryTimeActivity.KEY_DELIVERY_TIME_DATE);
            String time = data.getStringExtra(DeliveryTimeActivity.KEY_DELIVERY_TIME_TIME);
            deliveryTimeDate.setText(date);
            deliveryTimeTime.setText(time);

            long selectedTimestamp = data.getLongExtra(DeliveryTimeActivity.KEY_DELIVERY_TIME_TIMESTAMP, 0);
            OrderRequestBuilder.getInstance().setDeliveryTime(DateUtils.getOrderTimeFormat(selectedTimestamp));
        } else {
            OrderRequestBuilder.getInstance().setDeliveryTime("");
        }

        deliveryTimeDate.setVisibility(deliveryTimeType == DeliveryTimeActivity.TYPE_SPECIFIC_TIME ? View.VISIBLE : View.GONE);
        deliveryTimeTime.setVisibility(deliveryTimeType == DeliveryTimeActivity.TYPE_SPECIFIC_TIME ? View.VISIBLE : View.GONE);
    }

    public MutableLiveData<Boolean> getCreateOrderLiveData() {
        return createOrderLiveData;
    }

    public MutableLiveData<Boolean> getCreateOrderErrorLiveData() {
        return createOrderErrorLiveData;
    }
}
