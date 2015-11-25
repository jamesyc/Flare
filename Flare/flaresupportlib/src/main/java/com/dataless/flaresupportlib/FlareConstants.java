package com.dataless.flaresupportlib;

/**
 * Created by AlexJr on 11/25/15.
 */
public class FlareConstants {

    public static String START_STROBE = "START_STROBE";
    public static final String STOP_STROBE = "STOP_STROBE";
    public static final String TOGGLE_MODE = "TOGGLE_MODE";

    public static final String NEW_LOC_UPDATE = "NEW_LOC_UPDATE";

    public enum Turn {
        LEFT,
        RIGHT,
        STRAIGHT,
        SLIGHT_RIGHT,
        SLIGHT_LEFT,
        DESTINATION
    }
}