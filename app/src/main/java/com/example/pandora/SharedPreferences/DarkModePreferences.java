package com.example.pandora.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModePreferences {

    private static final String PREF_NAME = "dark_mode_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";

    private final SharedPreferences sharedPreferences;

    public DarkModePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean isEnabled) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_MODE, isEnabled).apply();
    }
}
