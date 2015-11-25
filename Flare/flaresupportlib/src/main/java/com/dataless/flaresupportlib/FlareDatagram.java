package com.dataless.flaresupportlib;

/**
 * Created by AlexJr on 11/25/15.
 */
public class FlareDatagram {

    public String messageType;

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
    
}
