package com.cs160.group14.flare;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by AlexJr on 11/23/15.
 */
public class wSignalingActivity extends WearableActivity{

    public static final String TAG = "WearSignallingActivity";

    LinearLayout mLayout;
    static int limitInSeconds = 30;
    static boolean sigBool = true;
    public static boolean stillRunning = false;
    int frequency = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signaling_layout);
        //mLayout = (LinearLayout) findViewById(R.id.signalingLayout);
        stillRunning = true;
        createAndStartTimer(frequency);
    }

    private void createAndStartTimer(final int frequency){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.signalingLayout);
                //boolean sigBool = true;
                int count = 0;
                while (true){
                    if (!stillRunning || count >  wSignalingActivity.limitInSeconds){
                        return;
                    }
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

    public static void stopStrobe(){
        wSignalingActivity.stillRunning = false;
    }
    }
