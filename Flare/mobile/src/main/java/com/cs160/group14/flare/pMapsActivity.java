package com.cs160.group14.flare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class pMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_maps);
        setUpMapIfNeeded();
        startService(new Intent(this, pMessageService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
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
}
