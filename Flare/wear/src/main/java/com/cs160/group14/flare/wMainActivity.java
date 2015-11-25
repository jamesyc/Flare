package com.cs160.group14.flare;

import android.content.Intent;
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
 * Created by AlexJr on 11/17/15
 * This is the leftmost screen (enter nav), the first one the user sees
 * Non-Nav Mode: Show "Enter destination" layout
 * Nav Mode: Show directions, update appropriately
 * Screen to the right of this should be the CurrentLocActivity
 */
public class wMainActivity extends WearableActivity {

    static final Class<?> rightActivity = CurrentLocActivity.class;
    GestureDetectorCompat mGestureDetector;
    static final String TAG = "wMainActivity";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_main);

        setUpGestureDetector();
        setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.testText);
        //Do mode specific things
        String mode = "navModeOff";
        if (WatchFlags.navModeOn){
            mode = "navModeOn";
        }
        mTextView.setText(mTextView.getText() + " " + mode);
        //Start the listener to listen for phone messages
        startService(new Intent(this, wListenerService.class));
    }

    public void setUpGestureDetector(){
        Log.d(TAG, "Set up gesture detector");
        SwipeGestureListener customListener =
                new SwipeGestureListener(this, rightActivity);
        this.mGestureDetector = new GestureDetectorCompat(this, customListener);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }



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
