package app.client.munchbear.munchbearclient.utils.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.annotations.NonNull;

import static android.support.constraint.Constraints.TAG;
import static app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel.FIREBASE_TOKEN;

/**
 * @author Roman H.
 */

public class FirebaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        registerFCMToken();
    }

    public void registerFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task ->
                handleOnRefreshTokenCompletedListener(task));
    }

    private void handleOnRefreshTokenCompletedListener(@NonNull Task<InstanceIdResult> task) {
        try {
            InstanceIdResult instanceIdResult = task.getResult();
            if (instanceIdResult != null) {
                String token = instanceIdResult.getToken();
                if (token != null) {
                    Data.Builder builder = new Data.Builder();
                    builder.putString(FIREBASE_TOKEN, token);
                    builder.build();

                    OneTimeWorkRequest work = new OneTimeWorkRequest
                            .Builder(RegisterFirebaseTokenWorker.class)
                            .setInputData(builder.build())
                            .build();
                    WorkManager.getInstance().enqueue(work);
                }
            }
        } catch (Exception e) {

        }
    }

}
