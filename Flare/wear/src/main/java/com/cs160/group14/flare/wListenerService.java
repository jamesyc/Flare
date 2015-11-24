package com.cs160.group14.flare;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by AlexJr on 11/23/15.
 */
public class wListenerService extends WearableListenerService {

    public static String TAG = "wListenerService";

    public static String START_STROBE = "START_STROBE";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
    }



    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Log.d(TAG, "onMessagedReceived called " + value);
        if (value.equalsIgnoreCase(wSignalingActivity.STOP_STROBE)){
            Intent stop_strobe_intent = new Intent(wSignalingActivity.STOP_STROBE);
            stop_strobe_intent.putExtra(wSignalingActivity.STOP_STROBE, wSignalingActivity.STOP_STROBE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(stop_strobe_intent);
        } else if (value.equalsIgnoreCase(START_STROBE)){
            if (wSignalingActivity.stillRunning == false) {
                startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }

    }
}
