package com.cs160.group14.flare;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs160.group14.flare.watchUtils.WatchFlags;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by AlexJr on 11/23/15.
 * This service listens for messages from the phone.
 */
public class wListenerService extends WearableListenerService {

    public static String TAG = "wListenerService";



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
        if (value.equalsIgnoreCase(WatchFlags.STOP_STROBE)){
            Intent stop_strobe_intent = new Intent(WatchFlags.STOP_STROBE);
            stop_strobe_intent.putExtra(WatchFlags.STOP_STROBE, WatchFlags.STOP_STROBE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(stop_strobe_intent);
            Log.d(TAG, "Sent stop strobe intent");
        } else if (value.equalsIgnoreCase(WatchFlags.START_STROBE)){
            if (wSignalingActivity.stillRunning == false) {
                startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }

    }
}
