package vip.mokardder.smsclient.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jaredrummler.android.device.DeviceName;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.apiInterface.API;
import vip.mokardder.smsclient.models.ResponseModel;
import vip.mokardder.smsclient.models.SendDataPayload;
import vip.mokardder.smsclient.retrofitClient.RetrofitClient;
import vip.mokardder.smsclient.services.DisplayOverService;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "Mokardder===>";


    Window popupWindow;

    TextView btn_start;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupWindow = new Window(this);
        checkOverlayPermission();

        setContentView(R.layout.activity_main);
        XXPermissions.with(this)
                // Request single permission
                .permission(Permission.SEND_SMS)
                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.READ_SMS)
                .permission(Permission.RECEIVE_SMS)

                .request((permissions, allGranted) -> {
                    if (!allGranted) {
                        toast("Some permissions were obtained successfully, but some permissions were not granted normally");
                        return;
                    }
                    toast("Granted");
                });

        btn_start = findViewById(R.id.start_service);



        btn_start.setOnClickListener(v -> {
            startService();
        });



        initFirebase();

//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK ->
//                            Toast.makeText(getBaseContext(), "Sms Sent", Toast.LENGTH_SHORT).show();
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
//                            Toast.makeText(getBaseContext(), "Generic failure",
//                                    Toast.LENGTH_SHORT).show();
//                    case SmsManager.RESULT_ERROR_NO_SERVICE ->
//                            Toast.makeText(getBaseContext(), "No service",
//                                    Toast.LENGTH_SHORT).show();
//                    case SmsManager.RESULT_ERROR_NULL_PDU ->
//                            Toast.makeText(getBaseContext(), "Null PDU",
//                                    Toast.LENGTH_SHORT).show();
//                    case SmsManager.RESULT_ERROR_RADIO_OFF ->
//                            Toast.makeText(getBaseContext(), "Radio off",
//                                    Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new IntentFilter(SENT));
//
//
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK ->
//                            Toast.makeText(getBaseContext(), "Sms Delivered", Toast.LENGTH_SHORT).show();
//                    case Activity.RESULT_CANCELED ->
//                            Toast.makeText(getBaseContext(), "SMS not delivered",
//                                    Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new IntentFilter(DELIVERED));


    }

    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if(Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, DisplayOverService.class));
                } else {
                    startService(new Intent(this, DisplayOverService.class));

                    popupWindow.open();
                }
            }
        }else{
            startService(new Intent(this, DisplayOverService.class));
        }
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }



    private void initFirebase() {
        FirebaseApp.initializeApp(this);


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    Log.d(TAG, "initFirebase: Token: " + task.getResult());
                    uploadFCM(getApplicationContext(), task.getResult(), getDeviceName(getBaseContext()));


                });





    }
    public void uploadFCM(Context c, String fcm, String  dName) {
        Log.d(TAG, "uploadFCM: Init");
        SendDataPayload payload = new SendDataPayload(dName, fcm, dName, "newRegister");
        RetrofitClient.getRetrofitInstance().create(API.class).uploadFcm(payload).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {


            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                Log.d(TAG, "onFailure: "+t);


            }
        });
    }
    public String getDeviceName(Context c){
        DeviceName.init(c);
        return DeviceName.getDeviceName();
    };


    private void toast(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }

}