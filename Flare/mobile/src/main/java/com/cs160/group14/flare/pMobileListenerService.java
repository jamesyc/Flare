package com.cs160.group14.flare;

import android.util.Log;

import com.dataless.flaresupportlib.FlareConstants;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by AlexJr on 11/23/15.
 */
public class pMobileListenerService extends WearableListenerService {

    public static final String TAG = "MobileListService";



    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "Received message: " + new String(messageEvent.getData(), StandardCharsets.UTF_8));
        if (messageEvent.getPath().equalsIgnoreCase(FlareConstants.TOGGLE_MODE)){
            Log.d(TAG, FlareConstants.TOGGLE_MODE + " received");
            handleStopNavEvent();
        }
    }

    public void handleStopNavEvent(){

    }
}
