package net.kaufmanndesigns.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.kaufmanndesigns.view.utils.Constants;

import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        ChangeTVStationFragment.SendMessage,
        LayoutFragment.GetViewModels,
        LoginFragment.sendLatestPreferencesToUI {

    public static final String TAG = "MainActivity";
    private ViewPager viewPager;
    public static Context sContext;
    boolean logged_in;
    int skipLogin = 0;
    private static Repository repository;

    NameViewModel nameViewModel;
    PreferencesViewModel preferencesViewModel;
    PreferencesParams preferencesParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");


        sContext = getApplicationContext();

        nameViewModel = ViewModelProviders.of(this).get(NameViewModel.class);                       //Instantiate NameViewModel Class used for passing Strings from RSS feed to this fragment
        preferencesViewModel = ViewModelProviders.of(this).get(PreferencesViewModel.class);         //PreferencesViewModel for database operations

        repository = new Repository(Objects.requireNonNull(getApplication()));


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        logged_in = sharedPreferences.getBoolean(Constants.KEY_LOGGED_IN, false);

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentStatePageAdapter adapter =
                new FragmentStatePageAdapter(getSupportFragmentManager());


        if(!logged_in){
            adapter.addFragment(new LoginFragment(), "LoginFragment");
            getSupportFragmentManager().beginTransaction().add(new LoginFragment(), "LoginTag").commit();
            Log.d(TAG, "setupViewPager: " + logged_in);
            skipLogin = 1;
        }

        adapter.addFragment(new LayoutFragment(),
                "LayoutFragment");

        getSupportFragmentManager().beginTransaction().add(new LayoutFragment(), "LayoutTag").commit();

        adapter.addFragment(new ChangeTVStationFragment(),
                "ChangeTVStationFragment");

        adapter.addFragment(new MusicChannelFragment(),
                "MusicChannelFragment");

        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber + skipLogin, true);
    }


    public void sendData(String channel, String url){
        LayoutFragment layoutFragment = (LayoutFragment) getSupportFragmentManager().findFragmentByTag("LayoutTag");
        assert layoutFragment != null;
        layoutFragment.setNewsURL(channel, url);
    }

//TODO pass around viewModels if wanted
    @Override
    public void getRepository() {
        LayoutFragment layoutFragment = (LayoutFragment) getSupportFragmentManager().findFragmentByTag("LayoutTag");
    }

    @Override
    public void removeLoginObserver() {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("LoginTag");
        Objects.requireNonNull(loginFragment).removeLoginObserver();
    }


    @Override
    public void sendLatestPreferences(JSONObject jsonObject) {
        LayoutFragment layoutFragment = (LayoutFragment) getSupportFragmentManager().findFragmentByTag("LayoutTag");
        Objects.requireNonNull(layoutFragment).loadPreviousPreferencesFromLogin(jsonObject);
    }
}