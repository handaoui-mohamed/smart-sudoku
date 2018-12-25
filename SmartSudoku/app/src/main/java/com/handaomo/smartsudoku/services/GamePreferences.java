package com.handaomo.smartsudoku.services;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

public class GamePreferences {
    static private GamePreferences instance = null;

    private GamePreferences(){}

    static public GamePreferences getInstance(){
        if(instance == null) instance = new GamePreferences();
        return instance;
    }

    public void setCurrentUser(Context context, String fullName){
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("fullName", fullName)
                .apply();
    }

    public String getCurrentUser(Context context){
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("fullName", "");
    }

    public int getGridSpacing(Context context){
        return Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("spacing", "5")) * 2;
    }

    public void removeCurrentUser(Context context) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("fullName", "")
                .apply();
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
                .putString("saved_timer", "" + (timer / 60 / 1000))
                .apply();
    }

    public long getCountDownTime(Context context, boolean newGame) {
        return Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(newGame ? "default_timer" : "saved_timer", "30")) * 60 * 1000;
    }
}
