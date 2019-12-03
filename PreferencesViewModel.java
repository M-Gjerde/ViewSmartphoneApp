package net.kaufmanndesigns.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesViewModel extends AndroidViewModel {

    private static final String TAG = "PreferencesViewModel";
    private static Repository repository;
    private LiveData<List<Preferences>> allPreferences;
    private LiveData<String> loginResponse;

    public PreferencesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allPreferences = repository.getPreferences();
    }

    void sendLoginRequest(String username, String password, NameViewModel nameViewModel){
        repository.sendLoginRequest(username, password, nameViewModel);
    }

    void sendJSONRequest(HashMap<String, String> params, NameViewModel nameViewModel){
        repository.getJsonRequest(params, nameViewModel);
    }

    void insert(Preferences preferences){
        repository.insert(preferences);
    }

     void update(Preferences preferences){
        repository.update(preferences);
    }

     void insertSettingsToServer(String url, Map<String, String> params) throws JSONException {
        repository.insertSettingsToServer(url, params);
    }

     static void getResultFromFeed(String url, NameViewModel nameViewModel) {
        repository.getResultFromFeed(url, nameViewModel);
    }

    public void deleteAllPreferences(){
        repository.deleteAllPreferences();
    }

     LiveData<List<Preferences>> getAllPreferences(){
        return allPreferences;
    }
}
