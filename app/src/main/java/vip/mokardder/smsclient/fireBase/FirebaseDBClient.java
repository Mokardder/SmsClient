package vip.mokardder.smsclient.fireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vip.mokardder.smsclient.interfaces.dacListener;
import vip.mokardder.smsclient.models.dacPayload;

public class FirebaseDBClient {
    private static FirebaseDatabase db;
    private static DatabaseReference dbRef;
    private static String PATH = "DAC";
    private static String TAG = "Mokardder--->";

    // Listener variable to hold the listener instance
    private static dacListener listener;

    public static void syncDac(String dac, String cashmemo) {
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference(PATH);
        dacPayload payload = new dacPayload(dac, cashmemo, String.valueOf(System.currentTimeMillis()));

        dbRef.setValue(payload).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DAC synchronized successfully");
            } else {
                Log.e(TAG, "DAC synchronization failed: " + task.getException());
            }
        });
    }

    public static void startDacMonitor() {
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference(PATH);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Convert the snapshot to the desired object using DataSnapshot's getValue method
                dacPayload payload = snapshot.getValue(dacPayload.class);

                if (payload != null) {
                    // Safely invoke listener only if it's not null
                    if (listener != null) {
                        listener.onDacReceived(payload);
                    } else {
                        Log.e(TAG, "Listener is not initialized");
                    }
                } else {
                    Log.d(TAG, "DAC Payload is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    // Method to set the listener
    public static void setDacListener(dacListener dacListener) {
        listener = dacListener;
    }
}
