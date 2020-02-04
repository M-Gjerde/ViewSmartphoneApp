package net.kaufmanndesigns.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.kaufmanndesigns.view.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class LayoutFragment extends Fragment {
    private static final String TAG = "LayoutFragment";
    private TextView news_headline;
    private TextView news_title;
    private TextView financeText;
    private TextView playbackText;
    private TextView calendarText;
    private TextView albumText;
    private TextView bottomTextView;
    private RelativeLayout loadingPanel;

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

        news_headline = view.findViewById(R.id.news_text);
        CardView bottomCard = view.findViewById(R.id.screen_saver);
        bottomTextView = view.findViewById(R.id.screen_saver_text);
        financeText = view.findViewById(R.id.finance_text);
        playbackText = view.findViewById(R.id.playback_text);
        calendarText = view.findViewById(R.id.calendar_text);
        albumText = view.findViewById(R.id.album_text);
        loadingPanel = view.findViewById(R.id.loadingPanel);

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

        news_headline.setOnClickListener(v ->
                ((MainActivity) getActivity()).setViewPager(1));

        news_title.setOnClickListener(view1 -> ((MainActivity) getActivity()).setViewPager(1));


        financeText.setOnClickListener(view12 -> {
            pref.setFinanceStatus(toggleBottomCard(financeText));
            preferencesViewModel.update(pref);
        });
        playbackText.setOnClickListener(view13 -> {
            pref.setPlaybackStatus(toggleBottomCard(playbackText));
            preferencesViewModel.update(pref);
        });

        AtomicBoolean longClick = new AtomicBoolean(false); //TODO Make this more readable
        calendarText.setOnClickListener(view14 -> {
            if (!longClick.get()) {
                pref.setCalendarStatus(toggleBottomCard(calendarText));
                preferencesViewModel.update(pref);
            }
            longClick.set(false);
        });

        calendarText.setOnLongClickListener(view15 -> {
            layoutFragmentInterface.addCalendarSetupFragment();
            longClick.set(true);
            return false;
        });

        albumText.setOnClickListener(view17 -> {
            pref.setSlideshow(toggleBottomCard(albumText));
            preferencesViewModel.update(pref);
        });


        bottomCard.setOnClickListener(view18 -> {
            pref.setPowerMode(toggleBottomCard(bottomTextView));
            preferencesViewModel.update(pref);
        });


        return view;
    }

    private String toggleBottomCard(TextView textView) {
        if (textView.getTag().toString().equals("transparent_underline")) {
            textView.setBackgroundResource(R.drawable.green_underline);
            textView.setTag(String.valueOf(R.drawable.green_underline));
            return "on";
        } else {
            textView.setBackgroundResource(R.drawable.transparent_underline);
            textView.setTag("transparent_underline");
            return "off";
        }

    }


    private void initializeAfterUpdate(String news_URL) {
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
                    if (pref.getUsername() != null) {
                        preferencesViewModel.insertSettingsToServer("https://app.kaufmanndesigns.net/db/view_app/insert_preferences.php", preferencesParams.getParams());
                        Log.d(TAG, "onChanged: " + preferencesParams.getParams());
                    }
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
            pref.setUsername(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.KEY_MIRROR_ID, null));
        };
        preferencesViewModel.getAllPreferences().observe(this, preferencesObserver);
    }

    Boolean dbNotEmpty() {
        return Objects.requireNonNull(preferencesViewModel.getAllPreferences().getValue()).size() > 0;
    }

    void loadPreviousPreferencesFromStartUp() {
        HashMap<String, String> params = new HashMap<>();
        String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.KEY_MIRROR_ID, null);
        params.put("getPreferences", "true");
        params.put("mirror_id", username);
        preferencesViewModel.sendJSONRequest(params, nameViewModel);
    }

    void previousPreferencesObserver() {
        final Observer<JSONObject> nameObserver = new Observer<JSONObject>() {
            @Override
            public void onChanged(@Nullable JSONObject jsonObject) {
                Log.d(TAG, "onChanged: previousObserver " + jsonObject.toString());
                try {
                    Log.d(TAG, "onChanged: " + jsonObject.get("power_mode"));
                    if (jsonObject.get("power_mode").equals("on")) {
                        pref.setPowerMode(toggleBottomCard(bottomTextView));
                    }
                    if (jsonObject.get("finance").equals("on")) {
                        pref.setFinanceStatus(toggleBottomCard(financeText));
                    }
                    if (jsonObject.get("slideshow").equals("on")) {
                        pref.setFinanceStatus(toggleBottomCard(albumText));
                    }
                    if (jsonObject.get("spotify_playback").equals("on")) {
                        pref.setFinanceStatus(toggleBottomCard(playbackText));
                    }
                    if (jsonObject.get("calendar").equals("on")) {
                        pref.setFinanceStatus(toggleBottomCard(calendarText));
                    }

                    setNewsURL(jsonObject.get("news_channel").toString(), jsonObject.get("news_url").toString());

                    preferencesViewModel.update(pref);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        nameViewModel.getJSONResponse().observe(this, nameObserver);
    }

    public void sendSetupData(String service_provider) {

        String url = "https://app.kaufmanndesigns.net/db/view_app/request_setup_information.php";
        String mirror_id = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.KEY_MIRROR_ID, null);
        String request = "setup";
        Log.d(TAG, "sendSetupData: " + service_provider);
        preferencesViewModel.sendSetupRequest(url, service_provider, mirror_id, request);
        if (service_provider.equals("outlook")) {

            setOutlookSetupURLObserver();
            loadingPanel.setVisibility(View.VISIBLE);
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String request = "outlookSetupURL";
                    preferencesViewModel.getOutlookSetupURL(url, mirror_id, request, nameViewModel);
                }
            }, 500);

        }
    }

    public void openOutlookURLInChrome(String url) {
        Log.d(TAG, "openOutlookURLInChrome: " + url);
        url = URLDecoder.decode(url);
        Uri uri = Uri.parse(url.trim());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, Objects.requireNonNull(getContext()).getPackageName());
        startActivity(intent);
    }

    public void setOutlookSetupURLObserver(){
        final Observer<String> nameObserver = newName -> {
            loadingPanel.setVisibility(View.GONE);
            openOutlookURLInChrome(Objects.requireNonNull(newName));
        };
        nameViewModel.getOutlookSetupURL().observe(this, nameObserver);
    }


    interface GetViewModels {
        void getRepository();

        void addCalendarSetupFragment();

        void removeCalendarSetupFragment();
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