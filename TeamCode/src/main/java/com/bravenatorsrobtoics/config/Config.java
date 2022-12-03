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

    private static final String RED_DISTANCE_OFF_WALL = "RedDistanceOffWall";
    private float redDistanceOffWall = 23.25f;
    public float GetRedDistanceOffWall() { return redDistanceOffWall; }
    public void SetRedDistanceOffWall(float v) { this.redDistanceOffWall = v; }

    private static final String RED_STRAFE_DISTANCE_TO_POLE = "RedStrafeDistanceToPole";
    private float redStrafeDistanceToPole = 15.25f;
    public float GetRedStrafeDistanceToPole() { return this.redStrafeDistanceToPole; }
    public void SetRedStrafeDistanceToPole(float v) { this.redStrafeDistanceToPole = v; }

    private static final String BLUE_DISTANCE_OFF_WALL = "BlueDistanceOffWall";
    private float blueDistanceOffWall = 23.25f;
    public float GetBlueDistanceOffWall() { return blueDistanceOffWall; }
    public void SetBlueDistanceOffWall(float v) { this.blueDistanceOffWall = v; }

    private static final String BLUE_STRAFE_DISTANCE_TO_POLE = "BlueStrafeDistanceToPole";
    private float blueStrafeDistanceToPole = 15.25f;
    public float GetBlueStrafeDistanceToPole() { return this.blueStrafeDistanceToPole; }
    public void SetBlueStrafeDistanceToPole(float v) { this.blueStrafeDistanceToPole = v; }

    public Config(Context context) {
        sp = context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        Load();
    }

    public void Load() {
        singleControllerOverride = sp.getBoolean(SINGLE_CONTROLLER_OVERRIDE, false);
        startingPosition = StartingPosition.toStartingPosition(sp.getString(STARTING_POSITION, StartingPosition.RED.name()));

        redDistanceOffWall = sp.getFloat(RED_DISTANCE_OFF_WALL, 23.25f);
        redStrafeDistanceToPole = sp.getFloat(RED_STRAFE_DISTANCE_TO_POLE, 15.25f);

        blueDistanceOffWall = sp.getFloat(BLUE_DISTANCE_OFF_WALL, 23.25f);
        blueStrafeDistanceToPole = sp.getFloat(BLUE_STRAFE_DISTANCE_TO_POLE, 15.25f);
    }

    public void Save() {
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(SINGLE_CONTROLLER_OVERRIDE, singleControllerOverride);
        editor.putString(STARTING_POSITION, startingPosition.name());
        editor.putFloat(RED_DISTANCE_OFF_WALL, redDistanceOffWall);
        editor.putFloat(RED_STRAFE_DISTANCE_TO_POLE, redStrafeDistanceToPole);
        editor.putFloat(BLUE_DISTANCE_OFF_WALL, blueDistanceOffWall);
        editor.putFloat(BLUE_STRAFE_DISTANCE_TO_POLE, blueStrafeDistanceToPole);

        editor.apply();
    }

    public void SetIsControllerOverride(boolean value) { Config.singleControllerOverride = value; }
    public boolean IsSingleControllerOverride() { return singleControllerOverride; }

    public void SetStartingPosition(StartingPosition startingPosition) { Config.startingPosition = startingPosition; }
    public StartingPosition GetStartingPosition() { return startingPosition; }

}
