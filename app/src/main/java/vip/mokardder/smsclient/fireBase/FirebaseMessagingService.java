package vip.mokardder.smsclient.fireBase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.database.SmsSentListDB;
import vip.mokardder.smsclient.jobService.sendSms;
import vip.mokardder.smsclient.models.FCMResponse;
import vip.mokardder.smsclient.ui.MainActivity;
import vip.mokardder.smsclient.utility.Utility;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static String TAG = "Mokardder===>";
    Gson gson = new Gson();

    SmsSentListDB db;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        db = new SmsSentListDB(getApplicationContext());

        if (remoteMessage.getData().size() > 0) {

            FCMResponse res = gson.fromJson(remoteMessage.getData().toString(), FCMResponse.class);


            Log.d(TAG, "onMessageReceived: " + res.getSmsData());


            db.addSms(res.getSmsData().getNumber(), res.getSmsData().getSmsMessage(), Utility.getCurrentTime());
            sendNotification("Sms Sent to -> " + res.getSmsData().getNumber());

            sendSmsJob(res.getSmsData().getNumber(), res.getSmsData().getSmsMessage());

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Sms Sent !")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendSmsJob(String num, String mes) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("uNumber", num);
        bundle.putString("uMessage", mes);
        ComponentName serviceComponent = new ComponentName(this, sendSms.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler deviceInfo = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        deviceInfo.schedule(builder.setExtras(bundle).build());
    }
}
