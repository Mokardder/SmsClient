package vip.mokardder.smsclient.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {
    public static String TAG = "Mokardder===>";
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    TextView btn_send;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        btn_send = findViewById(R.id.send_sms);


        btn_send.setOnClickListener(v -> {

        });


        initFirebase();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK ->
                            Toast.makeText(getBaseContext(), "Sms Sent", Toast.LENGTH_SHORT).show();
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                    case SmsManager.RESULT_ERROR_NO_SERVICE ->
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                    case SmsManager.RESULT_ERROR_NULL_PDU ->
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                    case SmsManager.RESULT_ERROR_RADIO_OFF ->
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(SENT));


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK ->
                            Toast.makeText(getBaseContext(), "Sms Delivered", Toast.LENGTH_SHORT).show();
                    case Activity.RESULT_CANCELED ->
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(DELIVERED));


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