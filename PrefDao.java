package net.kaufmanndesigns.view;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PrefDao {

    @Insert
    void insert(Preferences preferences);

    @Update
    void update(Preferences preferences);

    @Delete
    void delete(Preferences preferences);

    @Query("DELETE FROM Preferences_table")
    void deleteAllSettings ();

    @Query("SELECT * FROM Preferences_table ORDER BY id DESC")
    LiveData<List<Preferences>> getAllPreferences();
}
