package net.kaufmanndesigns.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LayoutFragment extends Fragment {
    private static final String TAG = "LayoutFragment";
    private TextView textView;
    private CardView mTVcard;
    private CardView bottomCard;
    private CardView musicCard;

    private NameViewModel nameViewModel;
    private PreferencesViewModel preferencesViewModel;

    public String news_URL;
    private Repository repository;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, container, false);
        Button musicChannelBtn = view.findViewById(R.id.change_music_button);
        mTVcard = view.findViewById(R.id.tv_stream_card);
        LinearLayout top_card = view.findViewById(R.id.top_card);
        textView = view.findViewById(R.id.news_title_text);
        bottomCard = view.findViewById(R.id.screen_saver);
        musicCard = view.findViewById(R.id.music_stream);
        textView.setSelected(true);

        nameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NameViewModel.class); //Instantiate NameViewModel Class
        preferencesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PreferencesViewModel.class);
        repository = new Repository(getActivity().getApplication(), nameViewModel);                                 //Instantiate the repository

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setViewPager(1);
            }
        });

        musicChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesViewModel.deleteAllPreferences();
            }
        });

        top_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Refresh
            }
        });

        bottomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        musicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        updatePreferences();
        setRSSNewsTitleOnUpdate();
        setRSSUpdater();

      return view;

    }

    //Functions for used in onCreate
    public void setRSSUpdater() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + news_URL);
                repository.getResultFromFeed("https://app.kaufmanndesigns.net/db/serveJson.php?site=" + news_URL);
            }
        }, 0, 1000*60*5);
    }

    private void setRSSNewsTitleOnUpdate(){
        // Create the observer which updates the UI.
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI, in this case, a TextView.
                textView.setText(newName);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        nameViewModel.getCurrentName().observe(this, nameObserver);
    }

    protected void setNewsURL(String channel, String url){
        Preferences preferences = new Preferences(channel, url);
        if (Objects.requireNonNull(preferencesViewModel.getAllPreferences().getValue()).size() != 0) {
            preferences.setId(preferencesViewModel.getAllPreferences().getValue().get(0).getId());
            preferencesViewModel.update(preferences);
        }
    }

    private void updatePreferences(){
        final Observer<List<Preferences>> preferencesObserver = new Observer<List<Preferences>>() {
            @Override
            public void onChanged(@Nullable List<Preferences> preferences) {
                assert preferences != null;
                if (!preferences.isEmpty()) news_URL = preferences.get(0).getUrl();
                else {
                    preferencesViewModel.insert(new Preferences("KaufmannDesigns", "DummyText"));
                }
                setRSSUpdater(); //Update the new URL after dataBase has been updated
            }
        };
        preferencesViewModel.getAllPreferences().observe(this, preferencesObserver);
    }
}