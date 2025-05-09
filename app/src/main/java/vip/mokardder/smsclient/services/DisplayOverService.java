package vip.mokardder.smsclient.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.fireBase.FirebaseDBClient;
import vip.mokardder.smsclient.ui.Window;

/**
 * Created by Kazi Md. Saidul Email: Kazimdsaidul@gmail.com  Mobile: +8801675349882 on 2020-01-01.
 */
public class DisplayOverService extends Service {
    private static final String TAG = "";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final long DELAYED_TIME = 1000;
    Window window;


    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDBClient  fireDB  = new FirebaseDBClient();

//        String input = .getStringExtra("inputExtra");

         window = new Window(this);
        window.open();

        fireDB.startDacMonitor();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        createNotificationChannel();

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ɱⱺⱪⲁɾɗɗⱸꝛ")
                .setContentText("sssss")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(null); // Passing null here silently fails





        //do heavy work on a background thread
        //stopSelf();


        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //REST OF CODE HERE//

                    notification.setContentText("DAC Displayer -> Ɱⱺⱪⲁɾɗɗⱸꝛ");
                    startForeground(1, notification.build());
                }

            }
        }).start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        window.open();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}