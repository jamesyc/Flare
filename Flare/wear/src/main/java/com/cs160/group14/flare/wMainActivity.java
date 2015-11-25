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
    BroadcastReceiver mMessageReceiver;
    static final String TAG = "wMainActivity";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpViews();

        setUpBroadcastReceiver();
        setUpGestureDetector();
        setAmbientEnabled();


        //Do mode specific things

        //Start the listener to listen for phone messages
        startService(new Intent(this, wListenerService.class));
    }

    /**
     * This should be called every time view comes up
     * Sets layout based on WatchFlags.
     */
    public void setUpViews(){
        if (WatchFlags.navModeOn){
            setContentView(R.layout.directions_layout);
            mTextView = (TextView) findViewById(R.id.directionsTextHolder);
            mContainerView = (BoxInsetLayout) findViewById(R.id.directionsContainer);
        } else {
            setContentView(R.layout.activity_w_main);
            mContainerView = (BoxInsetLayout) findViewById(R.id.container);
            mTextView = (TextView) findViewById(R.id.testText);
            mTextView.setText(mTextView.getText() + " " + "navModeOn");
        }
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

    public void setUpBroadcastReceiver(){
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Received broadcast: " + intent.getAction());
                if (intent.getAction().equalsIgnoreCase(WatchFlags.TOGGLE_MODE)){
                    Log.d(TAG, "Received Tog");
                    setUpViews();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(WatchFlags.TOGGLE_MODE));
        Log.d(TAG, "Finished setting up Broadcast receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpViews();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(WatchFlags.TOGGLE_MODE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

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
