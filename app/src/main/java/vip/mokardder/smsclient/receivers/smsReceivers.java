package vip.mokardder.smsclient.receivers;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsReceivers extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = smsMessage.getMessageBody();
                    String otp = extractOtp(messageBody);
                    if (otp != null) {
                        copyToClipboard(context, otp);
                    }
                }
            }
        }
    }

    private String extractOtp(String message) {
        // Regular expression to find 4 to 8 digit OTP
        Pattern pattern = Pattern.compile("\\b[0-9]{4,8}\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private void copyToClipboard(Context context, String otp) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("OTP", otp);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "DAC copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}

