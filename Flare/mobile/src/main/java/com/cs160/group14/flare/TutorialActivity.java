package com.cs160.group14.flare;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

/**
 * Created by AlexJr on 12/1/15.
 */
public class TutorialActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_view);
        getActionBar().setTitle("Flare Tutorial");
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eb9a60")));
    }

    public void onExitClick(View v){
        finish();
    }
}
