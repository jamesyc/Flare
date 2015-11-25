package com.cs160.group14.flare;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs160.group14.flare.watchUtils.WatchFlags;
import com.dataless.flaresupportlib.FlareConstants;
import com.dataless.flaresupportlib.FlareDatagram;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

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
        String path = messageEvent.getPath();
        String data2 = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Log.d(TAG, "Received :  " + path + " message from mobile");
        Log.d(TAG, "Data: " + data2);
        if (path.equalsIgnoreCase(FlareConstants.STOP_STROBE)){
            handleStopStrobe();
        } else if (path.equalsIgnoreCase(FlareConstants.START_STROBE)){
            handleStartStrobe();
        } else if (path.equalsIgnoreCase(FlareConstants.TOGGLE_MODE)){
            handleToggleNavMode();
        } else if (path.equalsIgnoreCase(FlareConstants.NEW_LOC_UPDATE)){
            String strData = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            FlareDatagram data = new Gson().fromJson(strData, FlareDatagram.class);
            handleLocUpdate(data);
        }

    }

    public void handleLocUpdate(FlareDatagram datagram){

    }

    public void handleToggleNavMode(){
        WatchFlags.navModeOn = !WatchFlags.navModeOn;
        broadCastNavmodeToggle();
    }

    public void broadCastNavmodeToggle(){
        Intent navmode_toggle_intent = new Intent(FlareConstants.TOGGLE_MODE);
        navmode_toggle_intent.putExtra(FlareConstants.TOGGLE_MODE,FlareConstants.TOGGLE_MODE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(navmode_toggle_intent);
        Log.d(TAG, "Sent NavMode Toggle broadcast");
    }

    public void handleStopStrobe(){
        Intent stop_strobe_intent = new Intent(FlareConstants.STOP_STROBE);
        stop_strobe_intent.putExtra(FlareConstants.STOP_STROBE, FlareConstants.STOP_STROBE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(stop_strobe_intent);
        Log.d(TAG, "Sent stop strobe intent broadcast");
    }

    public void handleStartStrobe(){
        if (wSignalingActivity.stillRunning == false) {
            startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
