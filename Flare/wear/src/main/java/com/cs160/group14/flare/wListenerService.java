package com.cs160.group14.flare;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by AlexJr on 11/23/15.
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
        startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
