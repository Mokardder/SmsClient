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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsReceivers extends BroadcastReceiver {

    private static final String TAG = "smsReceivers";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = createSmsMessage(pdu, bundle);
                    String messageBody = smsMessage.getMessageBody();
                    Log.d(TAG, "Received SMS: " + messageBody);
                    String otp = extractOtp(messageBody);

                    Log.d(TAG, "onReceive: " + extractOtp(messageBody));
                    if (otp != null) {

                        copyToClipboard(context, otp);
                    } else {
                        Log.d(TAG, "No OTP found in message");
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

    private String extractOtp(String message) {
        // Improved regex to capture DAC and invoice number
        Pattern pattern = Pattern.compile("Invoice Number # \\d+-\\d+ is (\\d{4})");
        Matcher matcher = pattern.matcher(message);


        if (matcher.find()) {
            String otp = matcher.group(1);
            Log.d(TAG, "extractOtp: " + otp);
        }

        if (matcher.find()) {
            String invoiceNumber = matcher.group();  // Group 1 captures the invoice number
       // Group 2 captures the DAC code

            return invoiceNumber;
        }
        return null;
    }

    private void copyToClipboard(Context context, String value) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);


        ClipData clip = ClipData.newPlainText("OTP", value);

        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "DAC Copied", Toast.LENGTH_SHORT).show(); // Show Toast for both cases

    }
}
