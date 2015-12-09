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
import com.dataless.flaresupportlib.FlareConstants;

/**
 * Created by AlexJr on 11/24/15.
 * This is the second screen to the left.
 * Exiting this should bring you to the leftmost screen
 * Mode: Non-Nav, could be either? MIGHT CHANGE TO EITHER
 * Screen to the right should be the gesture toggle screen
 */
public class CurrentLocActivity extends WearableActivity{

    static Class<?> rightActivity = wSignalingActivity.class;
    public static String TAG = "CurrentLocActivity";
    GestureDetectorCompat mGestureDetector;
    IntentFilter myFilter;
    private BroadcastReceiver mMessageReceiver;

    private BoxInsetLayout mContainerView;
    private TextView mTextView;

    public static String currStreet = "Curr Street: THIS SHOULD NEVER BE SEEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();

        setUpBroadcastReceiver();
        setUpGestureDetector();
        setAmbientEnabled();
    }

    /** Might have to add a second view to this, but for now,
     * regardless of whether navmode is on or not, we show current location n stuff
     * Note: current_loc_layout is for NAVMODE OFF; current_loc_layout2 is for NAVMODE ON (4 dots)
     * Setup currStreet
     */
    public void setUpViews(){
        String mode = "navModeOff";
        if (WatchFlags.navModeOn) {// <-- might want to add later if we add second layout to this
            setContentView(R.layout.current_loc_layout2);//4 dots
            mode = "navModeOn";
        } else {
            setContentView(R.layout.current_loc_layout);//3 dots
        }
        mContainerView = (BoxInsetLayout) findViewById(R.id.currentLocContainer);
//        mTextView = (TextView) findViewById(R.id.currentLocTextHolder);

        mTextView.setText(mTextView.getText() + " " + mode + "\n" + currStreet);
    }

    public void setRightActivity(){
        if (!WatchFlags.navModeOn){
            rightActivity = GestureToggleActivity.class;
        } else {
            rightActivity = TurnOffNavActivity.class;
        }
    }

    public void setUpBroadcastReceiver(){
        myFilter = new IntentFilter();
        myFilter.addAction(FlareConstants.NEW_LOC_UPDATE);
        myFilter.addAction(FlareConstants.TOGGLE_MODE);
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast: " + intent.getAction());
                if (intent.getAction().equalsIgnoreCase(FlareConstants.NEW_LOC_UPDATE)){
                    Log.d(TAG, "Received Loc Update");
                    setUpViews();
                } else if (intent.getAction().equalsIgnoreCase(FlareConstants.TOGGLE_MODE)){
                    setUpGestureDetector();
                    setUpViews();
                    Log.d(TAG, "Received Toggle Update");
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
        Log.d(TAG, "Finished setting up Broadcast receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpViews();
        setUpGestureDetector();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }
    /**Good to call this everytime we load to ensure correct order of views**/
    public void setUpGestureDetector(){
        setRightActivity();
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
