package com.cs160.group14.flare;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import android.support.annotation.Nullable;

import com.dataless.flaresupportlib.FlareConstants;
import com.dataless.flaresupportlib.FlareDatagram;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import org.w3c.dom.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.API;

/**
 * Created by james on 11/30/15.
 */
public class pNavService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "pNavService";

    public pNavService() {
    }

    private IBinder myBinder = new MyBinder();

//    private static final int LOCATION_INTERVAL = 1000;
//    private static final float LOCATION_DISTANCE = 10f;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    // Most recent location object, and LocationRequest object
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Navigation mode settings
    boolean navigationMode = false;
    LatLng destLatLng;
    Document directionsDoc;
    pGMapDirections md = null;
    String firstDirections;
    String firstDistText;
    String firstStepManeuver;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 5 sec
    private static int FASTEST_INTERVAL = 2000; // 2 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate service");
        if (checkPlayServices()) {
            if (mGoogleApiClient == null) {
                setUpAPIClientAndConnect();
            }
            createLocationRequest();
            pingLocation();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopLocationUpdates();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public pNavService getService() {
            return pNavService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind service");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return false;
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Set boolean flag
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");
        } else {
            // Set boolean flag
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
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

    public Location pingLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // mLastLocation is Location to be sent
        if (mLastLocation != null) {
            Log.d(TAG, "Location obtained!!!");
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Log.d(TAG, "Location currently lat:" + latitude + " lon:" + longitude);
        } else {
            // Couldn't get the location. Make sure location is enabled on the device
            Log.d(TAG, "Location is currently set to null! Darn.");
        }
        return mLastLocation;
    }

    public void setNav(boolean nMode, LatLng dLatLng, Document dDoc) {
        navigationMode = nMode;
        destLatLng = dLatLng;
        directionsDoc = dDoc;
    }

    public void pushDirections() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // mLastLocation is Location to be sent
        if (mLastLocation != null) {
            // Current location
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            LatLng currLatLng = new LatLng(latitude, longitude);

            // Get latest directions
            md = new pGMapDirections();
            directionsDoc = md.getDocument(currLatLng, destLatLng, pGMapDirections.MODE_BICYCLING);
            String firstDirectionsNew = md.getFirstHTMLInstructions(directionsDoc);
            String firstDistTextNew = md.getFirstDistanceText(directionsDoc);
            String firstStepManeuverNew = md.getFirstStepManeuver(directionsDoc);

            // Check if directions changed
            if (!firstDirectionsNew.equals(firstDirections) || !firstDistTextNew.equals(firstDistText)) {
                // New directions, push to watch
                firstDirections = firstDirectionsNew;
                firstDistText = firstDistTextNew;
                firstStepManeuver = firstStepManeuverNew;
                Log.d(TAG, "Direction update");
                Log.d(TAG, "Directions: " + firstDirections);//E.g: "Head east on Dwight way..."
                Log.d(TAG, "Distance: " + firstDistText);
                Log.d(TAG, "Maneuver: " + firstStepManeuver);//E.g: sharp left turn, head straight, etc.

                pMessageService.sendLocUpdate(firstDirections, firstDistText, firstStepManeuver);
            }
        } else {
            // Couldn't get the location. Make sure location is enabled on the device
        }
    }

    @Override
     public void onConnected(Bundle arg0) {
        Log.d(TAG, "onConnected service");
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
     }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        Log.d(TAG, "Location changed!");

        // TODO
        // Need to implement code that checks if we're at destination, and then turn off navigation
        // if (mLastLocation == destinationLocation)
        // navigationMode = false;

        // Get directions, send new location to watch!
        pingLocation();
        if (navigationMode) {
            pushDirections();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "---- Failed connection result: " + connectionResult.getErrorMessage());
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Log.d(TAG, "User disabled Google Play Services.");
            } else {
                Log.d(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}