package vip.mokardder.smsclient.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
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

    private static final String PREF_NAME = "MyPrefs";
    private static final String SELECTED_OPTION_KEY = "type";

    // Method to save the selected option in SharedPreferences
    public static void saveSelectedOption(Context context, int option) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SELECTED_OPTION_KEY, option);
        editor.apply(); // Apply the changes asynchronously
    }

    // Method to get the saved option from SharedPreferences
    public static int getOptions(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SELECTED_OPTION_KEY, 0); // Return -1 if no option is saved
    }
    public static boolean isOwner(Context c) {

        if (getOptions(c) == 1){
            return true;

        }else {
            return false;
        }

    }
}
