package app.client.munchbear.munchbearclient.utils.firebase;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Random;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.push.PushData;
import app.client.munchbear.munchbearclient.model.push.PushOrder;
import app.client.munchbear.munchbearclient.model.reservation.Reservation;
import app.client.munchbear.munchbearclient.view.MainActivity;

/**
 * @author Roman H.
 */

public class FBMessengerService extends FirebaseMessagingService {

    private static final String RESOURCE_RESERVATION = "reservations";
    private static final String RESOURCE_ORDER = "order";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Refresh push title"; //TODO Temp test title
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null){
            title = remoteMessage.getNotification().getTitle();
        }

        String message = "Refresh push message"; //TODO Temp test message
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null){
            message = remoteMessage.getNotification().getBody();
        }

        handleMessageType(parsePushRemoteMessage(remoteMessage), message, title);

    }

    private void handleMessageType(PushOrder pushOrder, String message, String title) {
        boolean isReservation = pushOrder.getResource().equals(RESOURCE_RESERVATION);

        if (!isForeground(getPackageName())) {
            sendNotification(message, title);
        } else {
            sendBroadcast(isReservation);
        }
    }

    private void sendBroadcast(boolean isReservation) {
        Intent intent = new Intent(Reservation.ACTION_RESERVATION_UPDATED);
        intent.putExtra(Reservation.KEY_IS_RESERVATION, isReservation);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /*
         * NotificationCompat.Builder for android os version >= Oreo
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            setupNotificationChannel(notificationManager);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                    getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);


            notificationManager.notify(getRequestCode(), builder.build());
        } else {
            /*
             * NotificationCompat.Builder for android os version < Oreo
             */
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(getRequestCode(), notificationBuilder.build());
        }
    }

    private void setupNotificationChannel(NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     *
     * @return generated random request code for notification
     */
    private static int getRequestCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    private PushOrder parsePushRemoteMessage(RemoteMessage remoteMessage) {
        JsonParser jsonParser = new JsonParser();
        JsonElement mJson = jsonParser.parse(remoteMessage.getData().toString());
        Gson gson = new Gson();
        PushData pushData = gson.fromJson(mJson, PushData.class);
        return pushData.getPushOrder();
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

}
