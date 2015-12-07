package com.cs160.group14.flare;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dataless.flaresupportlib.FlareConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Document;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.API;

/**
 * Created by AlexJr on 11/17/15
 * This is the main activity for mobile.
 * For testing I've put in buttons to trigger events on wear
 * Final product should just be a map with destination input
 */
public class pMapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    BroadcastReceiver mMessageReceiver;
    IntentFilter myFilter;

    private int counter;
    private int requestCode;
    // I have no clue what the following line does but it seems to be needed
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;

    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 33;

    // Binding pNavService
    pNavService pnav;
    boolean isBound = false;

    static final String TAG = "pMapsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Ask for ACCESS_FINE_LOCATION permissions
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "API version >=23, location is not enabled; need to request");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                Log.d(TAG, "API version >=23, location is enabled");
//                startService(new Intent(this, pNavService.class));
                Intent intent = new Intent(this, pNavService.class);
                bindService(intent, pNavConnection, Context.BIND_AUTO_CREATE);

            }
        } else {
            Log.d(TAG, "API version <23, location assumed enabled");
//            startService(new Intent(this, pNavService.class));
            Intent intent = new Intent(this, pNavService.class);
            bindService(intent, pNavConnection, Context.BIND_AUTO_CREATE);
        }

        setUpMapIfNeeded();
        setUpBroadcastReceiver();
        startService(new Intent(this, pMessageService.class));
        startService(new Intent(this, pMobileListenerService.class));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(API)
                .addApi(Wearable.API).addApi(LocationServices.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    ServiceConnection pNavConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            pnav = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            pNavService.MyBinder binder = (pNavService.MyBinder) service;
            pnav = binder.getService();
        }
    };

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "---- Failed connection result: " + connectionResult.getErrorMessage());
        mGoogleApiClient.connect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.d(TAG, "API version >=23, location onRequestPermissionsResult");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Log.d(TAG, "API version >=23, location permission granted");
//                    startService(new Intent(this, pNavService.class));
                    Intent intent = new Intent(this, pNavService.class);
                    bindService(intent, pNavConnection, Context.BIND_AUTO_CREATE);
                } else {
                    // permission denied, boo!
                    Log.d(TAG, "API version >=23, location permission denied");
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(13, 100)).title("Marker"));
    }

    public void sendStartStrobeButton(View v){
        //pMessageService.sendMessageToWear(MobileFlags.START_STROBE, MobileFlags.START_STROBE);
        pMessageService.sendStrobeStart();
    }

    public void sendEndStrobeButton(View v){
        //pMessageService.sendMessageToWear(MobileFlags.STOP_STROBE,MobileFlags.STOP_STROBE);
        pMessageService.sendStrobeStop();
    }

    public void sendToggleModeMessage(View v){
        //pMessageService.sendMessageToWear(MobileFlags.TOGGLE_MODE, MobileFlags.TOGGLE_MODE );
        pMessageService.sendToggleMessage();
    }

    public void sendLocationUpdateMessage(View v){
        /** THIS SHOULD BE CHANGED TO REFLECT THE ACTUAL DIRECITONS WE WANT TO SEND**/
        pMessageService.sendLocUpdate("Street " + counter++);
    }

    public void setUpBroadcastReceiver(){
        myFilter = new IntentFilter();
        myFilter.addAction(FlareConstants.TOGGLE_MODE);
        myFilter.addAction(FlareConstants.NEW_LOC_UPDATE);
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast: " + intent.getAction());
                if (intent.getAction().equalsIgnoreCase(FlareConstants.TOGGLE_MODE)){
                    Log.d(TAG, "Received Toggle");
                    handleNavToggle();
                } else if (intent.getAction().equalsIgnoreCase(FlareConstants.NEW_LOC_UPDATE)){
                    Log.d(TAG, "Received Loc Update");
                    handleLocUpdate();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, myFilter);
        Log.d(TAG, "Finished setting up Broadcast receiver");
    }

    public void handleNavToggle(){

    }

    public void handleLocUpdate(){
        /** Might be useful **/
    }

    public void selectDestination(View v){
        Log.d(TAG, "Launching select dest activity");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "google play error");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            Log.d(TAG, "Got place");
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Log.d(TAG, "Got place ok");

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();


                // private GoogleMap mMap;
                pGMapDirections md = new pGMapDirections();

                LatLng fromPosition = new LatLng(13.687140112679154, 100.53525868803263);
                LatLng toPosition = new LatLng(13.683660045847258, 100.53900808095932);


                Document doc = md.getDocument(fromPosition, toPosition, pGMapDirections.MODE_BICYCLING);
                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

                for(int i = 0 ; i < directionPoint.size() ; i++) {
                    rectLine.add(directionPoint.get(i));
                }

                mMap.addPolyline(rectLine);


            }
        }
    }

    public void launchTutorial(View v){
        Log.d(TAG, "Launching tutorial activity");
        startActivity(new Intent(this, TutorialActivity.class));

    }
}
