package vip.mokardder.smsclient.fireBase;

import static vip.mokardder.smsclient.utility.Utility.isOwner;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import vip.mokardder.smsclient.jobService.sendSms;
import vip.mokardder.smsclient.models.FCMResponse;
import vip.mokardder.smsclient.models.SmsData;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static String TAG = "Mokardder===>";
    Gson gson = new Gson();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (isOwner(getApplicationContext())) {

            if (remoteMessage.getData().size() > 0) {
                String res = gson.toJson(remoteMessage.getData());
                FCMResponse fcm = gson.fromJson(res, FCMResponse.class);
                for (SmsData data : fcm.getSmsData()) {
                    sendSmsJob(data.getNumber(), data.getSmsMessage());
                }
            }
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


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
