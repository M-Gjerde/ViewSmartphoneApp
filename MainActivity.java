package net.kaufmanndesigns.view;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.kaufmanndesigns.view.utils.Constants;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        ChangeTVStationFragment.SendMessage,
        LayoutFragment.GetViewModels,
        SetupCalendarFragment.SendSetupData{

    public static final String TAG = "MainActivity";
    private ViewPager viewPager;
    public static Context sContext;
    boolean logged_in;
    int skipLogin = 0;
    private static Repository repository;
    private FragmentStatePageAdapter adapter;

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
        LoginFragment loginFragment = new LoginFragment();
        LayoutFragment layoutFragment = new LayoutFragment();

        adapter = new FragmentStatePageAdapter(getSupportFragmentManager());


        if (!logged_in) {
            adapter.addFragment(loginFragment, "LoginFragment");
            skipLogin = 1;
        }

        adapter.addFragment(layoutFragment,
                "LayoutFragment");

        adapter.addFragment(new ChangeTVStationFragment(), //TODO Cleanup fragments
                "ChangeTVStationFragment");

        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber + skipLogin, true);
    }


    public void sendData(String channel, String url) {
        LayoutFragment layoutFragment = (LayoutFragment) adapter.getItem(adapter.getmFragmentTitleList().indexOf("LayoutFragment"));
        assert layoutFragment != null;
        layoutFragment.setNewsURL(channel, url);
    }

    //TODO pass around viewModels if wanted
    @Override
    public void getRepository() {
        LayoutFragment layoutFragment = (LayoutFragment) adapter.getItem(adapter.getmFragmentTitleList().indexOf("LayoutFragment"));
    }

    SetupCalendarFragment setupCalendarFragment;

    @Override
    public void addCalendarSetupFragment() {
        setupCalendarFragment = new SetupCalendarFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.calendar_setup_placeholder, setupCalendarFragment, "SetupCalendarFragment");
        ft.commit();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

    }

    @Override
    public void removeCalendarSetupFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (setupCalendarFragment != null) {
            transaction.remove(setupCalendarFragment);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            setupCalendarFragment = null;

        }
    }

    @Override
    public void sendSetupData(String service_provider) {
        LayoutFragment layoutFragment = (LayoutFragment) adapter.getItem(adapter.getmFragmentTitleList().indexOf("LayoutFragment"));
        assert layoutFragment != null;
        layoutFragment.sendSetupData(service_provider);
    }

    @Override
    public void removeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (setupCalendarFragment != null) {
            transaction.remove(setupCalendarFragment);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            setupCalendarFragment = null;

        }
    }
}