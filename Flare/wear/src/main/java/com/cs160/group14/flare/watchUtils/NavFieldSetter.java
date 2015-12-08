package com.cs160.group14.flare.watchUtils;

import com.cs160.group14.flare.CurrentLocActivity;
import com.cs160.group14.flare.wMainActivity;
import com.dataless.flaresupportlib.FlareDatagram;

/**
 * Created by AlexJr on 11/25/15.
 * This class should be called to set appropriate fields when receiving directions
 * datagrams.The logic for reminding activities to reload is NOT done here,
 * just setting of static string fields
 */
public class NavFieldSetter {

    public static void updateStreet(FlareDatagram data){
        if (data.currStreet.length() > 0){
//            wMainActivity.currStreet = data.currStreet;
            CurrentLocActivity.currStreet = data.currStreet;
        }
    }

    public static void updateDistanceToTurn(FlareDatagram data){
        if (data.distanceNextTurn.first){
            wMainActivity.distToTurn = data.distanceNextTurn.second;
        }
    }

    public static void updateTurnTypes(FlareDatagram data){
        if (data.currTurn.first){
            wMainActivity.currTurnType = data.currTurn.second;
        }
        if (data.nextTurn.first)
        {
            wMainActivity.nextTurnType = data.nextTurn.second;
        }
    }

    public static void updateNavigation(FlareDatagram data){
        updateStreet(data);
        updateDistanceToTurn(data);
        updateTurnTypes(data);
    }
}
