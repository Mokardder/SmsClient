package vip.mokardder.smsclient.ui;


import static vip.mokardder.smsclient.utility.Utility.isOwner;
import static vip.mokardder.smsclient.utility.Utility.saveSelectedOption;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;

import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import vip.mokardder.smsclient.utility.Utility;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "Mokardder===>";


    Window popupWindow;

    TextView btn_start;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        XXPermissions.with(this)
                // Request single permission
                .permission(Permission.SEND_SMS)
                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .permission(Permission.READ_SMS)
                .permission(Permission.POST_NOTIFICATIONS)
                .permission(Permission.RECEIVE_SMS)

                .request((permissions, allGranted) -> {
                    if (!allGranted) {
                        toast("Some permissions were obtained successfully, but some permissions were not granted normally");
                        return;
                    }
                    toast("Granted");
                });

        btn_start = findViewById(R.id.sentList);







        btn_start.setOnClickListener(v -> {
            Intent startList = new Intent(getApplicationContext(), SmsListActivity.class);
            startActivity(startList);
        });



        initFirebase();


        if (Utility.getOptions(this) == 0){
//            checkIfSender();
        }



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

    private void checkIfSender () {
        // Create the radio buttons
        RadioButton radioButton1 = new RadioButton(this);
        radioButton1.setText("Owner");

        RadioButton radioButton2 = new RadioButton(this);
        radioButton2.setText("Client");

        // Create a RadioGroup to hold the buttons
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);

        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Option");
        builder.setView(radioGroup);

        // Add "OK" button to the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            // Show a toast message based on the selected option
            if (selectedId == radioButton1.getId()) {
                Toast.makeText(MainActivity.this, "Owner", Toast.LENGTH_SHORT).show();
                saveSelectedOption(MainActivity.this, 1);
            } else if (selectedId == radioButton2.getId()) {
                Toast.makeText(MainActivity.this, "Client", Toast.LENGTH_SHORT).show();
                saveSelectedOption(MainActivity.this, 2);
            }
        });

        // Add "Cancel" button to the dialog
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }



    private void toast(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }

}