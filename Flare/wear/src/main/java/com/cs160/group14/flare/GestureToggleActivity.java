package com.cs160.group14.flare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cs160.group14.flare.watchUtils.SwipeGestureListener;
import com.cs160.group14.flare.watchUtils.WatchFlags;

/**
 * Created by AlexJr on 11/26/15.
 * This activity regulates the gesture sensing mode toggle screen
 * Corresponds to two different layouts (one saying we're on, one for off)
 */
public class GestureToggleActivity extends WearableActivity {

    /**RIGHT ACTIVITY SHOULD BE OPEN ON PHONE SCREEN **/
    static final Class<?> rightActivity = wSignalingActivity.class;
    public static String TAG = "GestureToggleActivity";
    GestureDetectorCompat mGestureDetector;
    IntentFilter myFilter;
    private BroadcastReceiver mMessageReceiver;

    private BoxInsetLayout mContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpView();

        setUpBroadcastReceiver();
        setUpGestureDetector();
        setAmbientEnabled();
    }

    public void onGestureToggleClick(View v){
        Log.d(TAG, "Gesture Toggle clicked");
        WatchFlags.gestureSensingOn = !WatchFlags.gestureSensingOn;
        setUpView();
    }

    public void setUpView(){
        if(WatchFlags.navModeOn) {//set four_of_4 dots, set version 2 layout
            if (WatchFlags.gestureSensingOn){
                setContentView(R.layout.gesture_tog_on_layout2);
                mContainerView = (BoxInsetLayout) findViewById(R.id.gestureTogOnContainer);
            } else{
                setContentView(R.layout.gesture_tog_off_layout2);
                mContainerView = (BoxInsetLayout) findViewById(R.id.gestureTogOffContainer);
            }
        } else {//set version 1, three_of_3 dots (original)
            if (WatchFlags.gestureSensingOn){
                setContentView(R.layout.gesture_tog_on_layout);
                mContainerView = (BoxInsetLayout) findViewById(R.id.gestureTogOnContainer);
            } else{
                setContentView(R.layout.gesture_tog_off_layout);
                mContainerView = (BoxInsetLayout) findViewById(R.id.gestureTogOffContainer);
            }
        }
    }

    //No real reason to have a broadcast receiver but lets set one up anyway
    public void setUpBroadcastReceiver(){
        myFilter = new IntentFilter();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast: " + intent.getAction());
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
        Log.d(TAG, "Finished setting up Broadcast receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

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
