package com.cs160.group14.flare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.cs160.group14.flare.watchUtils.WatchFlags;
import com.dataless.flaresupportlib.FlareConstants;

/**
 * Created by AlexJr on 11/23/15.
 */
public class wSignalingActivity extends WearableActivity{

    public static final String TAG = "WearSignallingActivity";

    public BroadcastReceiver mMessageReceiver;

    static boolean sigBool = true; //This determines which color to show next
    //public static boolean stillRunning = false;

    static int limitInSeconds = 30000;
    int frequency = 100;
    int color1 = Color.rgb(237,156,26); //orange
    int color2 = Color.WHITE; //gray

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!WatchFlags.gestureSensingOn){
            Log.d(TAG, "THIS IS NOT OKAY\nSIGNALING SHOULD NOT TURN ON WHILE GESTURE" +
                    "SENSING MODE IS OFF\nDESTROYING ACTIVITY");
            finish();
        }
        setContentView(R.layout.signaling_layout);
        WatchFlags.strobeIsOn = true;
        //stillRunning = true;
        setUpBroadcastReceiver();

        createAndStartTimer(frequency);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(FlareConstants.STOP_STROBE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //stillRunning = false;
        WatchFlags.strobeIsOn = false;
    }

    private void createAndStartTimer(final int frequency){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true){
                    if (!WatchFlags.strobeIsOn || count >  wSignalingActivity.limitInSeconds) {
                        WatchFlags.strobeIsOn = false;
                        finish();
                        return;
                    }
                    changeColor();
                     count+= frequency;
                    Log.d(TAG,Integer.toString(count)+String.valueOf(WatchFlags.strobeIsOn));
                    try{
                        Thread.sleep(frequency);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void changeColor(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.signalingLayout);
                Log.d(TAG, "Change Color called: " + wSignalingActivity.sigBool);
                if (wSignalingActivity.sigBool) { //One color
                    layout.setBackgroundColor(color1);
                } else {
                    layout.setBackgroundColor(color2);
                }
                wSignalingActivity.sigBool = !wSignalingActivity.sigBool;
            }
        });
        }


    public void setUpBroadcastReceiver(){
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Received Strobe destroy");
                WatchFlags.strobeIsOn = false;
                finish();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(FlareConstants.STOP_STROBE));
    }

    }
