package net.kaufmanndesigns.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kaufmanndesigns.view.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LayoutFragment extends Fragment {
    private static final String TAG = "LayoutFragment";
    private TextView news_headline;
    private TextView news_title;
    private CardView musicCard;

    public NameViewModel nameViewModel;
    private PreferencesViewModel preferencesViewModel;

    //Setup hashmap with preferences
    PreferencesParams preferencesParams;
    Preferences pref;

    GetViewModels layoutFragmentInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, container, false);

        CardView mTVcard = view.findViewById(R.id.tv_stream_card);
        news_headline = view.findViewById(R.id.news_text);
        CardView bottomCard = view.findViewById(R.id.screen_saver);
        TextView bottomTextView = view.findViewById(R.id.screen_saver_text);
        CardView financeCard = view.findViewById(R.id.finance_card);
        TextView financeText = view.findViewById(R.id.finance_text);
        TextView playbackText = view.findViewById(R.id.playback_text);
        TextView calendarText = view.findViewById(R.id.calendar_text);
        TextView albumText = view.findViewById(R.id.album_text);

        news_title = view.findViewById(R.id.news_title);

        news_headline.setSelected(true);
        layoutFragmentInterface.getRepository();

        nameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NameViewModel.class);   //Instantiate NameViewModel Class used for passing Strings from RSS feed to this fragment
        preferencesViewModel = ViewModelProviders.of(getActivity()).get(PreferencesViewModel.class);             //PreferencesViewModel for database operations

        previousPreferencesObserver();
        loadPreviousPreferencesFromStartUp();
        updatePreferences();
        setRSSNewsHeaderOnUpdate();

        Log.d(TAG, "onCreateView: " + pref);

        news_headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setViewPager(1);
            }
        });

        news_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setViewPager(1);
            }
        });

        final boolean[] financeState = {false};
        financeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (financeState[0]) {
                    financeText.setBackgroundResource(R.drawable.transparent_underline);
                    pref.setFinanceStatus("off");
                    financeState[0] = false;
                } else {
                    financeText.setBackgroundResource(R.drawable.green_underline);
                    pref.setFinanceStatus("on");
                    financeState[0] = true;
                }
            }
        });
        final boolean[] playbackState = {false};
        playbackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playbackState[0]) {
                    playbackText.setBackgroundResource(R.drawable.transparent_underline);
                    pref.setPlaybackStatus("off");
                    playbackState[0] = false;
                } else {
                    playbackText.setBackgroundResource(R.drawable.green_underline);
                    pref.setPlaybackStatus("on");
                    playbackState[0] = true;
                }
                preferencesViewModel.update(pref);
            }
        });
        final boolean[] calendarState = {false};
        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarState[0]) {
                    calendarText.setBackgroundResource(R.drawable.transparent_underline);
                    pref.setCalendarStatus("off");
                    calendarState[0] = false;
                } else {
                    calendarText.setBackgroundResource(R.drawable.green_underline);
                    pref.setCalendarStatus("on");
                    calendarState[0] = true;
                }
                preferencesViewModel.update(pref);
            }
        });
        final boolean[] albumState = {false};
        albumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (albumState[0]) {
                    albumText.setBackgroundResource(R.drawable.transparent_underline);
                    pref.setSlideshow("off");
                    albumState[0] = false;
                } else {
                    albumText.setBackgroundResource(R.drawable.green_underline);
                    pref.setSlideshow("on");
                    albumState[0] = true;
                }
                preferencesViewModel.update(pref);
            }
        });
        //TODO Syncronise this method with actual state of mirror
        final boolean[] standardState = {false};
        bottomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (standardState[0]) {
                    bottomTextView.setBackgroundResource(R.drawable.transparent_underline);
                    pref.setPowerMode("off");
                    standardState[0] = false;
                } else {
                    bottomTextView.setBackgroundResource(R.drawable.green_underline);
                    pref.setPowerMode("on");
                    standardState[0] = true;
                }
                preferencesViewModel.update(pref);

            }
        });


        return view;
    }


    private void initializeAfterUpdate(String news_URL){
        setRSSUpdater(news_URL);
        setNewsChannelTitle();
    }

    private void setNewsChannelTitle() {
        if (dbNotEmpty())
        news_title.setText(Objects.requireNonNull(
                preferencesViewModel.getAllPreferences().getValue()).get(0).getNewsChannel());
    }

    //Functions for used in onCreate
    public void setRSSUpdater(String news_URL) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PreferencesViewModel.getResultFromFeed("https://app.kaufmanndesigns.net/db/serveJson.php?site=" + news_URL, nameViewModel);
            }
        }, 0, 1000 * 60 * 2);
    }

    private void setRSSNewsHeaderOnUpdate() {
        // Create the observer which updates the UI.
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI, in this case, a TextView.
                news_headline.setText(newName);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        nameViewModel.getCurrentName().observe(this, nameObserver);
    }

    protected void setNewsURL(String channel, String url) {
        pref.setNewsChannel(channel);
        pref.setUrl(url);
        preferencesViewModel.update(pref);
    }

    private void updatePreferences() {
        final Observer<List<Preferences>> preferencesObserver = preferences -> {
            if (dbNotEmpty()) {
                try {
                    preferencesParams = new PreferencesParams(Objects.requireNonNull(preferences).get(0));
                    pref = preferences.get(0);
                    Log.d(TAG, "onChanged: Preference ID: " + preferences.get(0).getId());
                    Log.d(TAG, "onChanged: size(): " + preferences.size() );
                    Log.d(TAG, "onChanged: " +preferencesParams.getParams().get("username"));
                    Log.d(TAG, "onChanged: " + pref.getUsername());
                    if(preferencesParams.getParams().get("username")  != null){
                        preferencesViewModel.insertSettingsToServer("https://app.kaufmanndesigns.net/db/insert_preferences.php", preferencesParams.getParams());
                        Log.d(TAG, "onChanged: " + preferencesParams.getParams());                        }
                    if (dbNotEmpty())
                        initializeAfterUpdate(preferences.get(0).getUrl()); //Update the news URL after local dataBase has been updated
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pref = new Preferences("", "", "",
                        "", "",
                        "", "", "");
                preferencesViewModel.insert(pref);
            }
            pref.setUsername(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.KEY_MIRROR_ID,null));
        };
        preferencesViewModel.getAllPreferences().observe(this, preferencesObserver);
    }

    Boolean dbNotEmpty(){
        return Objects.requireNonNull(preferencesViewModel.getAllPreferences().getValue()).size() > 0;
    }

    void loadPreviousPreferencesFromLogin(JSONObject jsonObject){
        //Load news URL and channel | Power mode | spotify | calendar | newschannel| finance| slideshow
        Log.d(TAG, "loadPreviousPreferencesFromLogin: " + jsonObject.toString());
        try {
            setNewsURL(jsonObject.getString("news_channel"), jsonObject.getString("news_url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        layoutFragmentInterface.removeLoginObserver();
    }

    void loadPreviousPreferencesFromStartUp(){
        HashMap<String, String> params = new HashMap<>();
        String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.KEY_MIRROR_ID, null);
        params.put("getPreferences", "true");
        params.put("mirror_id", username);
        preferencesViewModel.sendJSONRequest(params, nameViewModel);
    }

    void previousPreferencesObserver(){
        final Observer<JSONObject> nameObserver = new Observer<JSONObject>() {
            @Override
            public void onChanged(@Nullable JSONObject jsonObject) {
                Log.d(TAG, "onChanged: previousObserver " + jsonObject.toString());
                //SetPrevousPreferences
            }
        };
        nameViewModel.getJSONResponse().observe(this, nameObserver);
    }


    interface GetViewModels{
        void getRepository();
        void removeLoginObserver();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            layoutFragmentInterface = (LayoutFragment.GetViewModels) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e);
            throw new ClassCastException(e.toString());
        }
    }

}