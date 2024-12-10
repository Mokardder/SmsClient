package vip.mokardder.smsclient.jobService;

import static vip.mokardder.smsclient.ui.MainActivity.TAG;
import static vip.mokardder.smsclient.utility.Utility.isOwner;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import com.jaredrummler.android.device.DeviceName;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vip.mokardder.smsclient.apiInterface.API;
import vip.mokardder.smsclient.models.ResponseModel;
import vip.mokardder.smsclient.models.SendDataPayload;
import vip.mokardder.smsclient.models.SendQoutaUpdate;
import vip.mokardder.smsclient.retrofitClient.RetrofitClient;

public class sendSms extends JobService {


    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {


        doTheThing(jobParameters);


        return true;
    }

    private void doTheThing(JobParameters params) {
        String uNum = params.getExtras().getString("uNumber");
        String uMes = params.getExtras().getString("uMessage");
        if (!jobCancelled) {
            sendDirectSMS(uNum, uMes);
        }

        jobFinished(params, false);
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        Log.d(TAG, "onStopJob: Job Cancelled");
        return true;
    }

    private void sendDirectSMS(String phoneNumber, String message) {


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE);

        //---when the SMS has been sent---
//        BroadcastReceiver sentReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK -> Toast.makeText(getBaseContext(), "Sms Sent", Toast.LENGTH_SHORT).show();
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
//        };
//        registerReceiver(sentReceiver, new IntentFilter(DELIVERED));
//
//        // |> when the SMS has been delivered ------->
//        BroadcastReceiver delivered = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "Sms Delivered", Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        };
//
//        registerReceiver(delivered, new IntentFilter(DELIVERED));


        if (message.length() > 20) {
            SmsManager sms = SmsManager.getDefault();

            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

            if (!isDayPassed()) {
                updateQoutaLocal(9);
            }


        } else {


            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            if (!isDayPassed()) {
                updateQoutaLocal(1);
            }


        }


        if (isOwner(getApplicationContext())) {

            updateQouta(getBaseContext(), getDeviceName(getBaseContext()));
        }




    }

    private boolean isDayPassed() {
        long savedMillis = getTime();
        if (savedMillis == 0) {
            storeTime();
            return true;
        }
        if (System.currentTimeMillis() >= savedMillis + 24 * 60 * 60 * 1000) {

            storeTime();
            return true;
        }
        return false;
    }


    private void storeTime() {
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();


        editor.putLong("time", System.currentTimeMillis());
        editor.putInt("qouta", 100);
        editor.apply();
    }

    private long getTime() {
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

        return prefs.getLong("time", 0);
    }

    private void updateQoutaLocal(int count) {
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();

        Log.d(TAG, "Get Qouta ?? terss: " + (getQOuta() - count));

        editor.putInt("qouta", getQOuta() - count);
        editor.apply();
    }

    private int getQOuta() {
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getInt("qouta", 0);
    }

    public void updateQouta(Context c, String dName) {
        Log.d(TAG, "uploadFCM: Init");
        SendQoutaUpdate payload = new SendQoutaUpdate(dName, "" + getQOuta(), "qouta");
        RetrofitClient.getRetrofitInstance().create(API.class).updateQouta(payload).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                Toast.makeText(c, "Device Registered : " + response.body().getStatus(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t);


            }
        });
    }

    public String getDeviceName(Context c) {
        DeviceName.init(c);
        return DeviceName.getDeviceName();
    }

    ;
}
