package com.dataless.flaresupportlib;

import android.util.Pair;

import com.google.gson.Gson;


/**
 * Created by AlexJr on 11/25/15.
 * This is the
 */
public class FlareDatagram {
    public String messageType;

    public String currStreet;

    /** If these have a false as first val, wear assumes no change **/
    public Pair<Boolean, String> distanceNextTurn;

    public Pair<Boolean, FlareConstants.Turn> currTurn;
    public Pair<Boolean, FlareConstants.Turn> nextTurn;

    public FlareDatagram(String messageType){
        this.messageType = messageType;
    }

    public static FlareDatagram makeStartStrobeGram(){
       return new FlareDatagram(FlareConstants.START_STROBE);
    }

    public static FlareDatagram makeStopStrobeDataGram(){
        return new FlareDatagram(FlareConstants.STOP_STROBE);
    }

    public static FlareDatagram makeToggleModeDataGram(){
        return new FlareDatagram(FlareConstants.TOGGLE_MODE);
    }

    private static FlareDatagram defaultLocUpdate(){
        FlareDatagram datagram = new FlareDatagram(FlareConstants.NEW_LOC_UPDATE);
        datagram.distanceNextTurn = new Pair<>(false, "0.0");
        datagram.currTurn = new Pair<>(false, FlareConstants.Turn.LEFT);
        datagram.nextTurn = new Pair<>(false, FlareConstants.Turn.LEFT);
        return datagram;
    }

    public static FlareDatagram makeLocUpdateDatagram(String currentStreet){
        FlareDatagram data = defaultLocUpdate();
        data.currStreet = currentStreet;

        /** ABSOLUTELY ADD WAY MORE INFO TO THIS**/
        return data;
    }

//    public static FlareDatagram makeStreetUpdateDatagram(String currentStreet){
//        FlareDatagram data = defaultLocUpdate();
//        data.currStreet = currentStreet;
//
//        return data;
//    }

    public String serializeMe(){
        return new Gson().toJson(this, FlareDatagram.class);
    }

    public static FlareDatagram deserialize(String json){
        return new Gson().fromJson(json, FlareDatagram.class);
    }
}
