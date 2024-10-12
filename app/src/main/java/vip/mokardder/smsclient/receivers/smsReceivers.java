package vip.mokardder.smsclient.receivers;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vip.mokardder.smsclient.fireBase.FirebaseDBClient;
import vip.mokardder.smsclient.models.dacPayload;
import vip.mokardder.smsclient.utility.Utility;

public class smsReceivers extends BroadcastReceiver {

    private static final String TAG = "smsReceivers";

    FirebaseDBClient fireDb = new FirebaseDBClient();
    @Override
    public void onReceive(Context context, Intent intent) {




        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = createSmsMessage(pdu, bundle);
                    String messageBody = smsMessage.getMessageBody();
                    String[] details = extractOtp(messageBody);
                    if (details != null) {
                        fireDb.syncDac(details[0], details[1]);
                        Utility.copyToClipboard (context, details[0]);
                    }
                }
            }
        }
    }

    private SmsMessage createSmsMessage(Object pdu, Bundle bundle) {
        SmsMessage smsMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
        } else {
            smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
        }
        return smsMessage;
    }

    private String[]
    extractOtp(String message) {

        String otp;
        String Cashmemo;
        // Improved regex to capture DAC and invoice number
        Pattern pattern = Pattern.compile("Invoice Number # (\\d+-\\d+) is (\\d{4})");
        Matcher matcher = pattern.matcher(message);


        if (matcher.find()) {

            otp = matcher.group(2);
            Cashmemo = matcher.group(1);
            return new String[]{otp, Cashmemo};
        }
        return null;
    }


}
