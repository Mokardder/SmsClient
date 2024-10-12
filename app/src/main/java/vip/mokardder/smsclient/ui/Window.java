package vip.mokardder.smsclient.ui;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.fireBase.FirebaseDBClient;
import vip.mokardder.smsclient.interfaces.dacListener;
import vip.mokardder.smsclient.models.dacPayload;
import vip.mokardder.smsclient.services.DisplayOverService;
import vip.mokardder.smsclient.utility.Utility;

public class Window {

    // declaring required variables
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;

    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    ImageButton closeBtn;

    public Window(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);

        }


        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.display_popup, null);

        closeBtn = mView.findViewById(R.id.closeButton);

        closeBtn.setOnClickListener(v -> {
            Toast.makeText(context, "Closed", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(context, DisplayOverService.class);
            context.stopService(serviceIntent);
            close();
        });
        TextView dacTextView = mView.findViewById(R.id.dacVL);
        TextView cashmemo = mView.findViewById(R.id.cashmemoVL);

        FirebaseDBClient.setDacListener(payload -> {
            if (mView != null) {
                mView.post(() -> {
                    boolean isOldDac = Utility.isOldDac(payload.getTimeStamp());
                    if (!isOldDac){
                        dacTextView.setText(payload.getDac());
                        cashmemo.setText(payload.getCashmemo());
                        Utility.copyToClipboard (context, payload.getDac());
                    }

                });
            }
        });
        mParams.gravity = Gravity.TOP;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);


        mView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Remember the initial position of the touch and the view's position
                    initialX = mParams.x;
                    initialY = mParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    // Calculate the new X and Y position
                    mParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    mParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                    // Update the view's position
                    mWindowManager.updateViewLayout(mView, mParams);
                    return true;
            }
            return false;
        });


    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }

    }


    public void close() {
        try {
            if (mView.getWindowToken() != null) {
                // Check if the view is attached before attempting to remove it
                if (mView.getParent() != null) {
                    mWindowManager.removeView(mView);
                }
            }
            // Invalidate the view to remove references
            mView.invalidate();
        } catch (Exception e) {
            Log.d("Error2", e.toString());
        }
    }


//    @Override
//    public void onDacReceived(dacPayload payload) {
//
//    }
}

