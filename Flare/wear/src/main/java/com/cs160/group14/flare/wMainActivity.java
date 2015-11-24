package com.cs160.group14.flare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class wMainActivity extends WearableActivity {

    GestureDetectorCompat mGestureDetector;
    static final String TAG = "wMainActivity";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_main);

        setUpGestureDetector();
        setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.testText);

        startService(new Intent(this, wListenerService.class));
        //setUpGestureDetector();
    }

    public void setUpGestureDetector(){
        Log.d(TAG, "Set up gesture detector");
        MyGestureListener lst = new MyGestureListener();
        lst.mParent = this;
        this.mGestureDetector = new GestureDetectorCompat(this, lst);
    }
    

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.d(TAG, "dispatchTouchEvent called");
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = wMainActivity.TAG + " Gestures";

        public Context mParent;



        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            //if (wSignalingActivity.stillRunning == false) {
                startActivity(new Intent(mParent, wSignalingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            //}
            return true;
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    public void updateDisplay(){
        if (isAmbient()){

        } else {

        }
    }
}
