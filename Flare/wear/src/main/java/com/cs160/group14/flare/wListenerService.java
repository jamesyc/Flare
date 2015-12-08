package com.cs160.group14.flare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs160.group14.flare.watchUtils.NavFieldSetter;
import com.cs160.group14.flare.watchUtils.WatchFlags;
import com.dataless.flaresupportlib.FlareConstants;
import com.dataless.flaresupportlib.FlareDatagram;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by AlexJr on 11/23/15.
 * This service listens for messages from the phone.
 * Messages are serialized json versions of FlareDatagrams
 */
public class wListenerService extends WearableListenerService  implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static String TAG = "wListenerService";
    private static final int CONNECTION_TIME_OUT_MS = 3000;
    public static String mobileNodeId;

    public static GoogleApiClient mGoogleApiClient;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        setUpAPIClientAndConnect();
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
            handleLocUpdate(FlareDatagram.deserialize(strData));
        }

    }

    public void handleLocUpdate(FlareDatagram datagram){
        //update appropriate static fields in classes
        NavFieldSetter.updateNavigation(datagram);
        // Need to broadcast that navigation information may have changed
        // (and screens need to reload their shit)
        Intent navUpdateIntent = new Intent(FlareConstants.NEW_LOC_UPDATE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(navUpdateIntent);
        Log.d(TAG, "Send navigation update broadcast");

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
        if (!WatchFlags.gestureSensingOn){
            Log.d(TAG, "Gesture sensing mode is off, aborting start strobe command");
            return;
        }
        if (WatchFlags.strobeIsOn == false) {
            startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void setUpAPIClientAndConnect(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "---- Set up API Client ----");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleAPIClientConnected!!");
        retrieveDeviceNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "---- Failed connection result: " + connectionResult.getErrorMessage());
        mGoogleApiClient.connect();
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wListenerService.mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(wListenerService.mGoogleApiClient).await();

                List<Node> nodes = result.getNodes();

                if (nodes.size() > 0) {
                    wListenerService.mobileNodeId = nodes.get(0).getId();
                }
                Log.d(TAG, "Device Node: " + mobileNodeId);
            }
        }).start();
    }

    public static void sendMessageToMobile(final String path, final String text){
        Log.d(TAG, "Attempting to send message to mobile");
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                Log.d(TAG, "sending to node" + text);
                if (nodes.getNodes().isEmpty()){
                    Log.d(TAG,  "No nodes found (maybe you're not connected?");
                    return;
                }
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodes.getNodes().get(0).getId(), path, text.getBytes() ).setResultCallback(
                        new ResultCallback() {
                            @Override
                            public void onResult(Result result) {
                                Log.d(TAG, "Sent message with result: " + result.getStatus().getStatusMessage());
                            }
                        }
                );
            }
        }).start();
    }

    public static void sendNavToggle(){
        sendMessageToMobile(FlareConstants.TOGGLE_MODE,
                FlareDatagram.makeToggleModeDataGram().serializeMe());
    }
}
