package net.kaufmanndesigns.view;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PreferencesParams {

    private static final String TAG = "PreferencesParams";
    private HashMap<String, String> params = new HashMap<>();

    private Preferences preferences;

    PreferencesParams(Preferences preferences){
        this.preferences = preferences;
        Log.d(TAG, "setNewsURL: Preference ID" + preferences.getId());

    }

    public void replaceParams(String key, String value){

    }

    public Preferences getPreferences() {
        return preferences;
    }

    public HashMap<String, String> getParams(){
        params.put("username", preferences.getUsername());
        params.put("news_url", preferences.getUrl());
        params.put("power_mode", preferences.getPowerMode());
        params.put("spotify_playback", preferences.getPlaybackStatus());
        params.put("calendar", preferences.getCalendarStatus());
        params.put("news_channel", preferences.getNewsChannel());
        params.put("finance", preferences.getFinanceStatus());
        params.put("slideshow", preferences.getSlideshow());

        return this.params;
    }

}
