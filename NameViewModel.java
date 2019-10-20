package net.kaufmanndesigns.view;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

public class NameViewModel extends ViewModel {
    private static final String TAG = "NameViewModel";
    // Create a LiveData with a String
    private MutableLiveData<String> currentName;
    public NameViewModel(){
        getCurrentName();
    }

    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        Log.d(TAG, "getCurrentName: " + currentName);
        return currentName;
    }


}

