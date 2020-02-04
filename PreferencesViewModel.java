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

    void sendSetupRequest(String url, String service_provider, String mirror_id, String request){
        repository.sendGoogleSetupRequest(url, service_provider, mirror_id, request);
    }

    void getOutlookSetupURL(String url, String mirror_id, String request, NameViewModel nameViewModel){
        Log.d(TAG, "getOutlookSetupURL: ");
         repository.getOutlookSetupURL(url, mirror_id, request, nameViewModel);
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
