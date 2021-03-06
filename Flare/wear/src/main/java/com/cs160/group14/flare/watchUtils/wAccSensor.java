package com.cs160.group14.flare.watchUtils;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs160.group14.flare.wSignalingActivity;
import com.dataless.flaresupportlib.FlareConstants;

/**
 * Created by AlexJr on 11/27/15.
 * This service is started by wMainActivity, listens for accelermeter events
 * When gesturesensing mode is on, we start/stop strobe activity appropriately
 */
public class wAccSensor extends Service implements SensorEventListener{

    static final String TAG = "Acc Service";

    SensorManager mSensorManager;
    Sensor mSensor;

    static float mAccelLast;
    static float mAccel;
    static float mAccelCurrent;
    static int TIME_THRESH = 1000;

    static double lasttime = new Double(0);//So that you can implement some kind of timer threshold
    boolean shakeMode = false;
    boolean leftMode = false;
    boolean rightMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        setUpSensor();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Long curr_time = System.currentTimeMillis();
        if (WatchFlags.gestureSensingOn && (curr_time > lasttime + TIME_THRESH)) {
            Log.d(TAG, "Sensor changed " + event.values[0] + "-" + event.values[1] + "-" + event.values[2]);
            lasttime = curr_time;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            if(x > 8.5 && !shakeMode && !leftMode) {
                Log.d(TAG, "Right Gesture position!!!");
                turnStrobeOn();
                rightMode = true;
            }
            else if (!shakeMode && !leftMode){
                turnStrobeOff();
                rightMode = false;
            }

            if(y < -8.5 && !shakeMode && !rightMode) {
                Log.d(TAG, "Left Gesture position!!!");
                turnStrobeOn();
                leftMode = true;
                // attempt to detect left gesture position
                // however some cyclist use vertical handrail which detected as left gesture
            }
            else if (!shakeMode && !rightMode){
                turnStrobeOff();
                leftMode = false;
            }


            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;
            // gForce will be close to 1 when there is no movement
            float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            float SHAKE_THRESHOLD = 1.1f;
            if(gForce > SHAKE_THRESHOLD && !WatchFlags.strobeIsOn) {
                Log.d(TAG, "SHAKEDD!!!");
                turnStrobeOn();
                shakeMode = true;
            }
            else if(gForce > SHAKE_THRESHOLD && WatchFlags.strobeIsOn)  {
                turnStrobeOff();
                shakeMode = false;
                leftMode = false;
                rightMode = false;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setUpSensor(){
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, Sensor.REPORTING_MODE_ON_CHANGE);
        Log.d(TAG, "setUpSensor");
    }

    /** Only turns strobe on when sensing mode is on **/
    public void turnStrobeOn(){
        if (!WatchFlags.gestureSensingOn){
            Log.d(TAG, "Gesture sensing mode is off, aborting start strobe command");
            return;
        }
        if (WatchFlags.strobeIsOn == false) {
            startActivity(new Intent(this, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /** Untested but this was copied from wListenerService which works **/
    public void turnStrobeOff(){
        Intent stop_strobe_intent = new Intent(FlareConstants.STOP_STROBE);
        stop_strobe_intent.putExtra(FlareConstants.STOP_STROBE, FlareConstants.STOP_STROBE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(stop_strobe_intent);
        Log.d(TAG, "Sent stop strobe intent broadcast");
    }
}
