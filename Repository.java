package net.kaufmanndesigns.view;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Repository {

    private static final String TAG = "Repository";
    private RequestQueue requestQueue;
    private PrefDao prefDao;
    private LiveData<List<Preferences>> preferences;

    Repository(Application application) {
        requestQueue = new RequestQueue(application.getApplicationContext());
        PreferencesDatabase preferencesDatabase = PreferencesDatabase.getInstance(application.getApplicationContext());
        prefDao = preferencesDatabase.prefDao();
        preferences = prefDao.getAllPreferences();
    }


    void insert(Preferences preferences) {
        new InsertPreferencesAsyncTask(prefDao).execute(preferences);
    }

    void update(Preferences preferences) {
        new UpdatePreferencesAsyncTask(prefDao).execute(preferences);
    }

    void deleteAllPreferences() {
        new DeleteAllPreferencesAsyncTask(prefDao).execute();
    }

    LiveData<List<Preferences>> getPreferences() {
        return preferences;
    }

    void getResultFromFeed(String url, NameViewModel nameViewModel) {
        new getRSSFeedAsyncTask(requestQueue, nameViewModel).execute(url);
    }

    void insertSettingsToServer(String url, Map<String, String> params) {
        new insertPreferencesToServerAsyncTask(requestQueue, params).execute(url);
    }

    void sendLoginRequest(String mirror_id, String password, NameViewModel nameViewModel) {
        String url = "https://app.kaufmanndesigns.net/db/view_app/login.php";
        new sendLoginRequestAsyncTask(requestQueue, mirror_id, password, nameViewModel).execute(url);
    }

    void getJsonRequest(HashMap<String, String> params, NameViewModel nameViewModel) {
        String url = "https://app.kaufmanndesigns.net/db/view_app/getPreferences.php";
        new getJsonRequestAsyncTask(requestQueue, params, nameViewModel).execute(url);
    }

    private static class getJsonRequestAsyncTask extends AsyncTask<String, Void, Void> {
        RequestQueue requestQueue;
        HashMap<String, String> params;
        NameViewModel nameViewModel;

        getJsonRequestAsyncTask(RequestQueue requestQueue, HashMap<String, String> params, NameViewModel nameViewModel) {
            this.requestQueue = requestQueue;
            this.params = params;
            this.nameViewModel = nameViewModel;
        }

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject parameters = new JSONObject(params);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strings[0], parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("streamers");
                        JSONObject latest_entry = jsonArray.getJSONObject(jsonArray.length() - 1);
                        nameViewModel.getJSONResponse().postValue(latest_entry);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }

            });
            requestQueue.addToRequestQueue(jsonObjectRequest);

            return null;

        }
    }

    private static class insertPreferencesToServerAsyncTask extends AsyncTask<String, Void, Void> {
        RequestQueue requestQueue;
        Map<String, String> params;

        private insertPreferencesToServerAsyncTask(RequestQueue requestQueue, Map<String, String> params) {
            this.requestQueue = requestQueue;
            this.params = params;
        }

        @Override
        protected Void doInBackground(String... strings) {
            StringRequest request = new StringRequest(Request.Method.POST, strings[0],
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse Insert: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            requestQueue.addToRequestQueue(request);
            return null;
        }
    }

    private static class getRSSFeedAsyncTask extends AsyncTask<String, Void, Void> {
        RequestQueue requestQueue;
        private NameViewModel nameViewModel;
        private ArrayList<String> newsTitles;

        private getRSSFeedAsyncTask(RequestQueue requestQueue, NameViewModel nameViewModel) {
            this.requestQueue = requestQueue;
            this.nameViewModel = nameViewModel;
            this.newsTitles = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... strings) {
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

    private static class InsertPreferencesAsyncTask extends AsyncTask<Preferences, Void, Void> {
        private PrefDao prefDao;

        private InsertPreferencesAsyncTask(PrefDao prefDao) {
            this.prefDao = prefDao;
        }

        @Override
        protected Void doInBackground(Preferences... preferences) {
            prefDao.insert(preferences[0]);
            Log.d(TAG, "doInBackground: Inserted " + preferences[0].getId());

            return null;
        }
    }

    private static class UpdatePreferencesAsyncTask extends AsyncTask<Preferences, Void, Void> {
        private PrefDao prefDao;

        private UpdatePreferencesAsyncTask(PrefDao prefDao) {
            this.prefDao = prefDao;
        }

        @Override
        protected Void doInBackground(Preferences... preferences) {
            prefDao.update(preferences[0]);

            return null;
        }
    }

    private static class DeleteAllPreferencesAsyncTask extends AsyncTask<Void, Void, Void> {
        private PrefDao prefDao;

        private DeleteAllPreferencesAsyncTask(PrefDao prefDao) {
            this.prefDao = prefDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prefDao.deleteAllSettings();
            return null;
        }
    }

    public static class sendLoginRequestAsyncTask extends AsyncTask<String, Void, Void> {
        RequestQueue requestQueue;
        private String mirror_id;
        private String password;
        String login_response;
        int mStatusCode;
        private NameViewModel nameViewModel;


        private sendLoginRequestAsyncTask(RequestQueue requestQueue, String mirror_id, String password, NameViewModel nameViewModel) {
            this.requestQueue = requestQueue;
            this.mirror_id = mirror_id;
            this.password = password;
            this.nameViewModel = nameViewModel;
        }

        @Override
        protected Void doInBackground(String... strings) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, strings[0], new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    login_response = response;
                    nameViewModel.getLoginResponse().postValue(response);
                }
            },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("volley", "Error: " + error.getMessage());
                        }
                    }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("login", "");
                    params.put("username", mirror_id);
                    params.put("password", password);
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    mStatusCode = response.statusCode;
                    Log.d(TAG, "parseNetworkResponse: " + mStatusCode);
                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.addToRequestQueue(stringRequest);
            return null;
        }


    }

}

