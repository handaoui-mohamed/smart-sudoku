package com.handaomo.smartsudoku.Services;

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

    public boolean isUserConnected(Context context){
        return getCurrentUser(context).equals("");
    }

    public void setGridSpacing(Context context, int spacing){
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putInt("spacing", spacing)
                .apply();
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
                .getString("savedGame", "");
    }

    public void saveGame(Context context, String config, String timer) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putString("savedGame", config+":"+timer)
                .apply();
    }
}
