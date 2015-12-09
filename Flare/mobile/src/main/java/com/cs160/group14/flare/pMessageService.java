package com.cs160.group14.flare;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.dataless.flaresupportlib.FlareConstants;
import com.dataless.flaresupportlib.FlareDatagram;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.API;

/**
 * Created by AlexJr on 11/23/15.
 */
public class pMessageService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static GoogleApiClient mGoogleApiClient;


    public static final String TAG = "pMessageService";

    public static String wearNodeId;

    private static final int CONNECTION_TIME_OUT_MS = 3000;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpAPIClientAndConnect();
    }

    public void setUpAPIClientAndConnect(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(API)
                .addApi(Wearable.API).addApi(LocationServices.API)
                .addScope(Drive.SCOPE_FILE)
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
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected())mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "---- Failed connection result: " + connectionResult.getErrorMessage());
        mGoogleApiClient.connect();
    }

    private void retrieveDeviceNode() {
        GoogleApiClient client = mGoogleApiClient;
        new Thread(new Runnable() {
            @Override
            public void run() {
                pMessageService.mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(pMessageService.mGoogleApiClient).await();

                List<Node> nodes = result.getNodes();

                if (nodes.size() > 0) {
                    pMessageService.wearNodeId = nodes.get(0).getId();
                }
                Log.d(TAG, "Device Node: " + wearNodeId);
            }
        }).start();
    }

    public static void sendMessageToWear(final String path, final String text){
        Log.d(TAG, "Attempting to send message to wear");
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                //for(Node node : nodes.getNodes()) {
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

    public static void sendStrobeStop(){
        sendMessageToWear(FlareConstants.STOP_STROBE, FlareDatagram.makeStopStrobeDataGram().serializeMe());
    }

    public static void sendStrobeStart(){
        sendMessageToWear(FlareConstants.START_STROBE, FlareDatagram.makeStartStrobeGram().serializeMe());
    }

    public static void sendToggleMessage(){
        sendMessageToWear(FlareConstants.TOGGLE_MODE,
                FlareDatagram.makeToggleModeDataGram().serializeMe());
    }

    public static void sendNavOnMessage(){
        sendMessageToWear(FlareConstants.NAV_MODE_ON,
                FlareDatagram.makeToggleModeDataGram().serializeMe());
    }

    /**This is how we push location updates to the wear.
     * Gson serializes everything, you just need to fill in the right arguments
     * and update the FlareDatagram class accordingly
     *
     * Note: CHANGE - add arguments instead of just taking "First Directions"
     * Problem: (MUST) Setting current street? WEAR should call NavFieldSetter to update CurrLocActivity.
     */
    public static void sendLocUpdate(String firstDirections, String firstDistText, String firstStepManeuver) {//added arguments
        //Create FlareDatagram loaded with Direction information
        FlareDatagram datagram = FlareDatagram.makeLocUpdateDatagram(firstDirections);
        datagram.currStreet = "2530 Ridge Road";

        datagram.distanceNextTurn = new Pair<>(true, firstDistText);//load with distance
        if (!firstStepManeuver.equalsIgnoreCase("No Maneuver")) {//if there is a turn, parse firstStepManeuver
            ArrayList<String> directions;
            directions = new ArrayList<String>(Arrays.asList(firstStepManeuver.split("-")));//eg: turn-sharp-left
            if (directions.contains("left")) {
                datagram.currTurn = new Pair<>(true, FlareConstants.Turn.LEFT);
            } else if (directions.contains("right")) {
                datagram.currTurn = new Pair<>(true, FlareConstants.Turn.RIGHT);
            }
        }
        //test send datagram to wear: manual set currTurn, wear SUCCESSFULLY retrieves.
//        datagram.currTurn = new Pair<>(true, FlareConstants.Turn.RIGHT);
        sendMessageToWear(FlareConstants.NEW_LOC_UPDATE, datagram.serializeMe());
//        sendMessageToWear(FlareConstants.NEW_LOC_UPDATE,
//                FlareDatagram.makeLocUpdateDatagram("current street").serializeMe());//makeLocUpdateDatagram(takes Current Street!)
    }
}
