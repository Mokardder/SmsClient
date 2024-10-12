package vip.mokardder.smsclient.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class Utility {



    public static Boolean isOldDac (String timestamp) {

        long converted = Long.parseLong(timestamp);

        long currentMillis = System.currentTimeMillis();
        long differenceMillis = currentMillis - converted;


        long differenceInSeconds = 50 * 1000;

        if (Math.abs(differenceMillis) >= differenceInSeconds) {
            return true;
        } else {
            return false;
        }

    }

    public static void copyToClipboard(Context context, String value) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);


        ClipData clip = ClipData.newPlainText("OTP", value);

        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "DAC Copied", Toast.LENGTH_SHORT).show(); // Show Toast for both cases

    }
}
