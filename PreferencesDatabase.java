package net.kaufmanndesigns.view;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = Preferences.class, version = 2, exportSchema = false)
public abstract class PreferencesDatabase extends RoomDatabase {

    public static PreferencesDatabase instance;

    public abstract PrefDao prefDao();

    public static synchronized PreferencesDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PreferencesDatabase.class, "preferences_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;

    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private PrefDao prefDao;

        private PopulateDBAsyncTask(PreferencesDatabase preferencesDatabase){
            prefDao = preferencesDatabase.prefDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prefDao.insert(new Preferences("KaufmannDesigns1", "https://app.kaufmanndesigns.net/db/noInternet"));
            return null;
        }
    }
}
