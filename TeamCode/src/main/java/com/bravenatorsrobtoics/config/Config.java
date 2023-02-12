package com.bravenatorsrobtoics.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String PREFERENCES_ID = "RobotPrefPowerPlay9533";
    private SharedPreferences sp;

    private static final String SINGLE_CONTROLLER_OVERRIDE = "SingleControllerOverride";
    private static boolean singleControllerOverride;

    private static final String STARTING_POSITION = "STARTING_POSITION";
    private static StartingPosition startingPosition;
    public enum StartingPosition {
        RED, BLUE;

        public static StartingPosition toStartingPosition(String textPosition) {
            try {
                return valueOf(textPosition);
            } catch(Exception e) {
                return RED; // TODO: Throw Warning
            }
        }
    }

    public Config(Context context) {
        sp = context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        Load();
    }

    public void Load() {
        singleControllerOverride = sp.getBoolean(SINGLE_CONTROLLER_OVERRIDE, false);
        startingPosition = StartingPosition.toStartingPosition(sp.getString(STARTING_POSITION, StartingPosition.RED.name()));
    }

    public void Save() {
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(SINGLE_CONTROLLER_OVERRIDE, singleControllerOverride);
        editor.putString(STARTING_POSITION, startingPosition.name());

        editor.apply();
    }

    public void SetIsControllerOverride(boolean value) { Config.singleControllerOverride = value; }
    public boolean IsSingleControllerOverride() { return singleControllerOverride; }

    public void SetStartingPosition(StartingPosition startingPosition) { Config.startingPosition = startingPosition; }
    public StartingPosition GetStartingPosition() { return startingPosition; }

}
