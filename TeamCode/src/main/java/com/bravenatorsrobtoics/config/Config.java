package com.bravenatorsrobtoics.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String PREFERENCES_ID = "RobotPrefPowerPlay9533";
    private SharedPreferences sp;

    public static final String SINGLE_CONTROLLER_OVERRIDE = "SingleControllerOverride";
    public static boolean _singleControllerOverride;

    public Config(Context context) {
        sp = context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        Load();
    }

    public void Load() {
        _singleControllerOverride = sp.getBoolean(SINGLE_CONTROLLER_OVERRIDE, false);
    }

    public void Save() {
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(SINGLE_CONTROLLER_OVERRIDE, _singleControllerOverride);

        editor.apply();
    }

    public boolean IsSingleControllerOverride() { return _singleControllerOverride; }

}
