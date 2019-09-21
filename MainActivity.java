package net.kaufmanndesigns.view;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private FragmentStatePagerAdapter mFragmentStatePagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");

        viewPager = findViewById(R.id.view_pager);

        setupViewPager(viewPager);


    }

    private void setupViewPager (ViewPager viewPager){
        FragmentStatePagerAdapter adapter =
                new FragmentStatePageAdapter(getSupportFragmentManager());

        ((FragmentStatePageAdapter) adapter).addFragment(new LayoutFragment(),
                "LayoutFragment");
        ((FragmentStatePageAdapter) adapter).addFragment(new ChangeTVStationFragment(),
                "ChangeTVStationFragment");

        ((FragmentStatePageAdapter) adapter).addFragment(new ViewLayoutEditorFragment(),
                "ViewLayoutEditorFragment");

        ((FragmentStatePageAdapter) adapter).addFragment(new MusicChannelFragment(),
                "MusicChannelFragment" );

        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }
}
