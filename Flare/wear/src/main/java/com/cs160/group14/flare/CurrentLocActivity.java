package com.cs160.group14.flare;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cs160.group14.flare.watchUtils.SwipeGestureListener;
import com.cs160.group14.flare.watchUtils.WatchFlags;

/**
 * Created by AlexJr on 11/24/15.
 * This is the second screen to the left.
 * Exiting this should bring you to the leftmost screen
 * Mode: Non-Nav, could be either? MIGHT CHANGE TO EITHER
 * Screen to the right should be the gesture toggle screen
 */
public class CurrentLocActivity extends WearableActivity{

    static final Class<?> rightActivity = wSignalingActivity.class;
    public static String TAG = "CurrentLocActivity";
    GestureDetectorCompat mGestureDetector;

    private BoxInsetLayout mContainerView;
    private TextView mTextView;

    public static String currStreet = "THIS SHOULD NEVER BE SEEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_loc_layout);

        setUpGestureDetector();
        setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.currentLocContainer);
        mTextView = (TextView) findViewById(R.id.currentLocTextHolder);
        String mode = "navModeOff";
        if (WatchFlags.navModeOn){
            mode = "navModeOn";
        }
        mTextView.setText(mTextView.getText() + " " + mode);
    }

    public void setUpGestureDetector(){
        Log.d(TAG, "Set up gesture detector");
        SwipeGestureListener customListener = new SwipeGestureListener(this, rightActivity);
        this.mGestureDetector = new GestureDetectorCompat(this, customListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }



    /** Everything after this is for Ambience**/
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    public void updateDisplay(){
        if (isAmbient()){

        } else {

        }
    }
}
