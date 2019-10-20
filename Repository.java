package net.kaufmanndesigns.view;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class Repository {

    private static final String TAG = "Repository";
    private RequestQueue requestQueue;
    private NameViewModel nameViewModel;

    private PrefDao prefDao;
    private LiveData<List<Preferences>> preferences;

    Repository(Application application, NameViewModel nameViewModel) {
        requestQueue = new RequestQueue(application.getApplicationContext());
        this.nameViewModel = nameViewModel;
        PreferencesDatabase preferencesDatabase = PreferencesDatabase.getInstance(application.getApplicationContext());
        prefDao = preferencesDatabase.prefDao();
        preferences = prefDao.getAllPreferences();
    }

    public void insert(Preferences preferences){
        new InsertPreferencesAsyncTask(prefDao).execute(preferences);
    }

    public void update(Preferences preferences){
        new UpdatePreferencesAsyncTask(prefDao).execute(preferences);
    }

    public void delete(Preferences preferences){

    }

    public void deleteAllPreferences(){
        new DeleteAllPreferencesAsyncTask(prefDao).execute();
    }

    public LiveData<List<Preferences>> getPreferences() {
        return preferences;
    }

    void getResultFromFeed(String url) {
        new getRSSFeedAsyncTask(requestQueue, nameViewModel).execute(url);
    }
    private static class getRSSFeedAsyncTask extends AsyncTask<String, Void, Void> {
        RequestQueue requestQueue;
        private NameViewModel nameViewModel;

        private getRSSFeedAsyncTask(RequestQueue requestQueue, NameViewModel nameViewModel) {
            this.requestQueue = requestQueue;
            this.nameViewModel = nameViewModel;
        }

        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<String> newsTitles = new ArrayList<>();
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET, strings[0], new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    newsTitles.add(parseJSONObjectResponse(response, i));
                                }
                                nameViewModel.getCurrentName().postValue(newsTitles.get(1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                }
            });
            requestQueue.addToRequestQueue(jsonRequest);

            return null;
        }


        private String parseJSONObjectResponse(JSONObject response, int i) throws JSONException {
            JSONObject jsonObject = new JSONObject(response.getString("channel"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("item");
            JSONObject titles = jsonArray.getJSONObject(i);
            return titles.getString("title");
        }
    }

    private static class InsertPreferencesAsyncTask extends AsyncTask <Preferences, Void, Void> {
        private PrefDao prefDao;

        private InsertPreferencesAsyncTask(PrefDao prefDao){
            this.prefDao = prefDao;
        }
        @Override
        protected Void doInBackground(Preferences... preferences) {
            prefDao.insert(preferences[0]);

            return null;
        }
    }

    private static class UpdatePreferencesAsyncTask extends AsyncTask <Preferences, Void, Void> {
        private PrefDao prefDao;

        private UpdatePreferencesAsyncTask(PrefDao prefDao){
            this.prefDao = prefDao;
        }
        @Override
        protected Void doInBackground(Preferences... preferences) {
            prefDao.update(preferences[0]);

            return null;
        }
    }

    private static class DeleteAllPreferencesAsyncTask extends AsyncTask <Void, Void, Void> {
        private PrefDao prefDao;

        private DeleteAllPreferencesAsyncTask(PrefDao prefDao){
            this.prefDao = prefDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prefDao.deleteAllSettings();
            return null;
        }
    }

}

