package net.kaufmanndesigns.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ChangeTVStationFragment.SendMessage {

    public static final String TAG = "MainActivity";
    private ViewPager viewPager;
    public static Context sContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        sContext = getApplicationContext();

    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentStatePageAdapter adapter =
                new FragmentStatePageAdapter(getSupportFragmentManager());

        adapter.addFragment(new LayoutFragment(),
                "LayoutFragment");

        getSupportFragmentManager().beginTransaction().add(new LayoutFragment(), "LayoutTag").commit();

        adapter.addFragment(new ChangeTVStationFragment(),
                "ChangeTVStationFragment");

        adapter.addFragment(new ViewLayoutEditorFragment(),
                "ViewLayoutEditorFragment");

        adapter.addFragment(new MusicChannelFragment(),
                "MusicChannelFragment");

        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }


    public static Context getContext() {
        return sContext;
    }

    public void sendData(String channel, String url){
        LayoutFragment layoutFragment = (LayoutFragment) getSupportFragmentManager().findFragmentByTag("LayoutTag");
        assert layoutFragment != null;
        layoutFragment.setNewsURL(channel, url);
    }

}