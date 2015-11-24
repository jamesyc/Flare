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

/**
 * Created by AlexJr on 11/23/15.
 */
public class wSignalingActivity extends WearableActivity{

    public static final String TAG = "WearSignallingActivity";
    public static final String STOP_STROBE = "STOP_STROBE";
    public BroadcastReceiver mMessageReceiver;

    static boolean sigBool = true; //This determines which color to show next
    public static boolean stillRunning = false;

    static int limitInSeconds = 30;
    int frequency = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signaling_layout);
        stillRunning = true;
        setUpBroadcastReceiver();

        createAndStartTimer(frequency);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(STOP_STROBE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        stillRunning = false;
    }

    private void createAndStartTimer(final int frequency){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true){
                    if (!stillRunning || count >  wSignalingActivity.limitInSeconds)return;
                    changeColor();
                     count+= (frequency) / 1000.0;
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
                    layout.setBackgroundColor(Color.RED);
                } else {
                    layout.setBackgroundColor(Color.YELLOW);
                }
                wSignalingActivity.sigBool = !wSignalingActivity.sigBool;
            }
        });
        }
    /*
    public static void stopStrobe(){
        wSignalingActivity.stillRunning = false;
    }*/

    public void setUpBroadcastReceiver(){
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Received Strobe destroy");
                finish();
            }
        };
        //registerReceiver(mMessageReceiver, new IntentFilter(STOP_STROBE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(STOP_STROBE));
    }

    }
