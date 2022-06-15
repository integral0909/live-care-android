package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.response.CardListResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CardListFragmentViewModel extends ProfileViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<CreditCard>> creditCardListLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> creditCardListErrorLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> deleteCardLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void getCardList() {
        ApiClient.getRepository().getCardList(CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CardListResponse>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        compositeDisposable.add(disposable);
                    }

                    @Override
                    public void onSuccess(Response<CardListResponse> cardListResponseResponse) {
                        if (cardListResponseResponse.isSuccessful()) {
                            if (cardListResponseResponse.body() != null && cardListResponseResponse.body().getCreditCardList().size() > 0) {
                                creditCardListLiveData.setValue(cardListResponseResponse.body().getCreditCardList());
                            }
                        } else {
                            creditCardListErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        creditCardListErrorLiveData.setValue(true);
                    }
                });
    }

    public void deleteCard(int cardId) {
        ApiClient.getRepository().deleteCard(cardId, CorePreferencesImpl.getCorePreferences().getAccessToken())
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
                            deleteCardLiveData.setValue(true);
                        } else {
                            deleteCardLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        deleteCardLiveData.setValue(false);
                    }
                });
    }

    public MutableLiveData<List<CreditCard>> getCreditCardListLiveData() {
        return creditCardListLiveData;
    }

    public MutableLiveData<Boolean> getCreditCardListErrorLiveData() {
        return creditCardListErrorLiveData;
    }

    public MutableLiveData<Boolean> getDeleteCardLiveData() {
        return deleteCardLiveData;
    }
}
