package net.kaufmanndesigns.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

public class NameViewModel extends AndroidViewModel {
    private static final String TAG = "NameViewModel";
    // Create a LiveData with a String
    private MutableLiveData<String> currentName;
    private MutableLiveData<String> loginResponse;
    private MutableLiveData<JSONObject> JSONResponse;

    public NameViewModel(@NonNull Application application) {
        super(application);

        getCurrentName();
        getLoginResponse();
        getJSONResponse();
    }

    MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<>();
        }
        Log.d(TAG, "getCurrentName: " + currentName);
        return currentName;
    }

     MutableLiveData<String> getLoginResponse(){
        if(loginResponse == null)loginResponse = new MutableLiveData<>();
         Log.d(TAG, "getLoginResponse: " + loginResponse);
        return loginResponse;
    }

    MutableLiveData<JSONObject> getJSONResponse(){
        if(JSONResponse == null)JSONResponse = new MutableLiveData<>();
        Log.d(TAG, "getLoginResponse: " + JSONResponse);
        return JSONResponse;
    }


}

