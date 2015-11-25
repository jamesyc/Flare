package com.dataless.flaresupportlib;

/**
 * Created by AlexJr on 11/25/15.
 */
public class FlareDatagram {

    public String messageType;

    public String currStreet;

    /** If these aren't set, wear assumes no change **/
    public double distanceNextTurn;
    public FlareConstants.Turn currTurn;
    public FlareConstants.Turn nextTurn;

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

    public static FlareDatagram makeLocUpdateDatagram(String currentStreet){
        FlareDatagram data = new FlareDatagram(FlareConstants.NEW_LOC_UPDATE);
        data.currStreet = currentStreet;

        /** ABSOLUTELY ADD WAY MORE INFO TO THIS**/
        return data;
    }
}
