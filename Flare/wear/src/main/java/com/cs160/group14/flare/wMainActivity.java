package com.cs160.group14.flare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cs160.group14.flare.watchUtils.SwipeGestureListener;

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

    }

    public void setUpGestureDetector(){
        Log.d(TAG, "Set up gesture detector");
        SwipeGestureListener lst = new SwipeGestureListener(this, wSignalingActivity.class);
        this.mGestureDetector = new GestureDetectorCompat(this, lst);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
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
