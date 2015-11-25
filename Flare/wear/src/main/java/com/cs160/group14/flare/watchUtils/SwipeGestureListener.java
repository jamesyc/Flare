package com.cs160.group14.flare.watchUtils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by AlexJr on 11/24/15.
 */
public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG =  "SwipeGestureListener";

    public Context mParent;
    public Class<?> rightSwipeClass;

    boolean debugPrint = false;

    public SwipeGestureListener(Context mParent, Class<?> rightSwipeClass){
        super();
        this.mParent = mParent;
        this.rightSwipeClass = rightSwipeClass;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        if (debugPrint) Log.d(TAG, "onDown: " + event.toString());
        return true;
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        if (debugPrint) Log.d(TAG, "onFling: " + event1.toString()+event2.toString());
        mParent.startActivity(new Intent(mParent, rightSwipeClass).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        return true;
    }
}