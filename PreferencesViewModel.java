package net.kaufmanndesigns.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class PreferencesViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Preferences>> allPreferences;
    public PreferencesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application, null);
        allPreferences = repository.getPreferences();
    }

    public void insert(Preferences preferences){
        repository.insert(preferences);
    }

    public void update(Preferences preferences){
        repository.update(preferences);
    }

    public void deleteAllPreferences(){
        repository.deleteAllPreferences();
    }

    public LiveData<List<Preferences>> getAllPreferences(){
        return allPreferences;
    }
}
