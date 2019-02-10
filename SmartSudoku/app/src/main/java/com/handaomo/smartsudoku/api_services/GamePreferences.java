package com.handaomo.smartsudoku.api_services;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

public class GamePreferences {
    static private GamePreferences instance = null;

    private GamePreferences() {
    }

    static public GamePreferences getInstance() {
        if (instance == null) instance = new GamePreferences();
        return instance;
    }

    public void setCurrentUser(Context context, String fullName) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("full_name", fullName)
                .apply();
    }

    public String getCurrentUser(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("full_name", "");
    }

    public void removeCurrentUser(Context context) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("full_name", "")
                .apply();
    }

    public int getGridSpacing(Context context) {
        return Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("spacing", "5")) * 2;
    }

    public String getSavedGame(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("saved_config", "");
    }

    public void saveGame(Context context, String config, long timer) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("saved_config", config)
                .putString("saved_timer", "" + timer)
                .apply();
    }

    public long getCountDownTime(Context context, boolean newGame) {
        if (newGame) {
            return Long.parseLong(PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getString("default_timer", "30")) * 60 * 1000;
        }

        return Long.parseLong(PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getString("saved_timer", "60000"));
    }

    public String getLastUpdateDate(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("last_update_date", "2019-02-01T21:16:00.000000Z");
    }

    public void setLastUpdateDate(Context context, String date) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("last_update_date", date)
                .apply();
    }
}
