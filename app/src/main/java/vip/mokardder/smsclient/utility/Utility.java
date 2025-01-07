package vip.mokardder.smsclient.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

    public static String getCurrentTime () {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = sdf.format(calendar.getTime());
        return formattedDateTime  ;
    }

    public static <T> Object processResponse(String base64String, Class<T> modelClass) {
        try {
            // Decode the Base64 string into bytes
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convert the decoded bytes to a UTF-8 string (ensure proper encoding)
            String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);

            // Use Gson to parse the decoded JSON string
            Gson gson = new Gson();
            JsonElement jsonElement = JsonParser.parseString(decodedJson);

            // If JSON is an array, return List<T>
            if (jsonElement.isJsonArray()) {
                Type listType = TypeToken.getParameterized(List.class, modelClass).getType();
                return gson.fromJson(jsonElement, listType);
            }

            // If JSON is an object, return T
            if (jsonElement.isJsonObject()) {
                return gson.fromJson(jsonElement, modelClass);
            }

            // Throw an exception if the JSON type is unexpected
            throw new IllegalStateException("Unexpected JSON type: " + jsonElement);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Or handle error as needed
        }
    }
    public static boolean isOwner(Context c) {

        if (getOptions(c) == 1){
            return true;

        }else {
            return false;
        }

    }
}
