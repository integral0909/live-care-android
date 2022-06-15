package app.client.munchbear.munchbearclient.utils.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import app.client.munchbear.munchbearclient.model.request.FCMTokenRequest;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel.FIREBASE_TOKEN;

/**
 * @author Roman H.
 */

public class RegisterFirebaseTokenWorker extends Worker {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RegisterFirebaseTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String token = getInputData().getString(FIREBASE_TOKEN);
        FCMTokenRequest requestModel = new FCMTokenRequest(token);

        ApiClient.getRepository().connectFCMToken(requestModel, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e("TAG", "Token refreshed");
                        } else {
                            Log.e("TAG", "Token error");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "Token error");
                    }
                });

        return Result.SUCCESS;

    }
}
