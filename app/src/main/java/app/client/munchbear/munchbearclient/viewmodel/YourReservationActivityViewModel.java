package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.request.UpdateReservationRequest;
import app.client.munchbear.munchbearclient.model.reservation.Reservation;
import app.client.munchbear.munchbearclient.model.request.CreateReservationRequestModel;
import app.client.munchbear.munchbearclient.model.response.CreateReservationResponse;
import app.client.munchbear.munchbearclient.model.response.UpdateReservationResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Roman H.
 */

public class YourReservationActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Create reservation Live data
    private MutableLiveData<Reservation> createdReservationLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> createReservationErrorLiveData = new MutableLiveData<>();

    //Cancel reservation Live data
    private MutableLiveData<Boolean> cancelReservationLivaData = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelReservationErrorLiveData = new MutableLiveData<>();

    //Update reservation Live Data
    private MutableLiveData<Reservation> updateReservationLiveData = new MutableLiveData();
    private MutableLiveData<Boolean> updateReservationErrorLiveData = new MutableLiveData();

    private MutableLiveData<Integer> notValidFields = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void handleCreateReservation(int restaurantId, boolean isDateSelected, CreateReservationRequestModel requestModel) {
        if (isRequestModelValid(restaurantId, isDateSelected, requestModel)) {
            ApiClient.getRepository().createReservation(restaurantId,
                    CorePreferencesImpl.getCorePreferences().getAccessToken(), requestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<CreateReservationResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(Response<CreateReservationResponse> createReservationResponseResponse) {
                            if (createReservationResponseResponse.isSuccessful()) {
                                Reservation reservation = createReservationResponseResponse.body().getReservation();
                                createdReservationLiveData.setValue(reservation);
                            } else {
                                createReservationErrorLiveData.setValue(true);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            createReservationErrorLiveData.setValue(true);
                        }
                    });
        } else {
            createReservationErrorLiveData.setValue(false);
        }
    }

    private boolean isRequestModelValid(int restaurantId, boolean isDateSelected, CreateReservationRequestModel requestModel) {
        return checkStringValue(requestModel.getName(), R.string.res_not_valid_name)
                && checkStringValue(requestModel.getPhone(), R.string.res_not_valid_phone)
                && checkIntValue(restaurantId, R.string.res_not_valid_restaurant)
                && checkDate(isDateSelected)
                && checkIntValue(requestModel.getAmountOfPeople(), R.string.res_not_valid_people_amount);
    }

    private boolean checkDate(boolean value) {
        if (!value) {
            notValidFields.setValue(R.string.res_not_valid_date);
        }
        return value;
    }

    private boolean checkStringValue(String value, int notValidString) {
        if (!value.isEmpty()) {
            return true;
        } else {
            notValidFields.setValue(notValidString);
            return false;
        }
    }

    private boolean checkIntValue(int value, int notValidString) {
        if (value > 0) {
            return true;
        } else {
            notValidFields.setValue(notValidString);
            return false;
        }
    }

    public void cancelReservation(int reservationId) {
        ApiClient.getRepository().cancelReservation(reservationId, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            cancelReservationLivaData.setValue(true);
                        } else {
                            cancelReservationErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        cancelReservationErrorLiveData.setValue(true);
                    }
                });
    }

    public void updateReservation(int reservationId, String name, String phone) {
        UpdateReservationRequest updateResModel = new UpdateReservationRequest(name, phone);
        ApiClient.getRepository().updateReservation(reservationId, CorePreferencesImpl.getCorePreferences().getAccessToken(),
                updateResModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<UpdateReservationResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<UpdateReservationResponse> reservationResponse) {
                        if (reservationResponse.isSuccessful()) {
                            updateReservationLiveData.setValue(reservationResponse.body().getReservation());
                        } else {
                            updateReservationErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateReservationErrorLiveData.setValue(false);
                    }
                });
    }

    public MutableLiveData<Reservation> getCreatedReservationLiveData() {
        return createdReservationLiveData;
    }

    public MutableLiveData<Boolean> getCreateReservationErrorLiveData() {
        return createReservationErrorLiveData;
    }

    public MutableLiveData<Integer> getNotValidFields() {
        return notValidFields;
    }

    public MutableLiveData<Boolean> getCancelReservationLivaData() {
        return cancelReservationLivaData;
    }

    public MutableLiveData<Boolean> getCancelReservationErrorLiveData() {
        return cancelReservationErrorLiveData;
    }

    public MutableLiveData<Reservation> getUpdateReservationLiveData() {
        return updateReservationLiveData;
    }

    public MutableLiveData<Boolean> getUpdateReservationErrorLiveData() {
        return updateReservationErrorLiveData;
    }
}
