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

import com.cs160.group14.flare.watchUtils.SwipeGestureListener;
import com.cs160.group14.flare.watchUtils.WatchFlags;
import com.dataless.flaresupportlib.FlareConstants;

/**
 * Created by AlexJr on 11/25/15.
 */
public class TurnOffNavActivity extends WearableActivity {

    /**RIGHT ACTIVITY SHOULD BE GESTURE SENSE MODE TOGGLE SCREEN **/
    static final Class<?> rightActivity = wSignalingActivity.class;
    public static String TAG = "TurnOffNavActivity";
    GestureDetectorCompat mGestureDetector;
    IntentFilter myFilter;
    private BroadcastReceiver mMessageReceiver;

    private BoxInsetLayout mContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!WatchFlags.navModeOn) {
            Log.d(TAG, "THIS IS NOT OKAY\nTHIS ACTIVITY SHOULD NOT BE CREATED\n" +
                    "WHEN NAVMODE IS OFF");
            finish();
        }
        setUpView();

        setUpBroadcastReceiver();
        setUpGestureDetector();
        setAmbientEnabled();
    }

    public void setUpView(){
        setContentView(R.layout.exitnavmode_layout);
        mContainerView = (BoxInsetLayout) findViewById(R.id.turnOffNavContainer);

    }

    public void setUpBroadcastReceiver(){
        myFilter = new IntentFilter();
        myFilter.addAction(FlareConstants.TOGGLE_MODE);
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast: " + intent.getAction());
                if (intent.getAction().equalsIgnoreCase(FlareConstants.TOGGLE_MODE)){
                    Log.d(TAG, "Received toggle");
                    if (!WatchFlags.navModeOn) finish();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
        Log.d(TAG, "Finished setting up Broadcast receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!WatchFlags.navModeOn){
            finish();
        }
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
