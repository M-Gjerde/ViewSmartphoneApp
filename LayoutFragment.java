package net.kaufmanndesigns.view;

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
import android.widget.Toast;

public class LayoutFragment extends Fragment {
    private static final String TAG = "LayoutFragment";

    private Button TVChannelBtn;
    private Button musicChannelBtn;
    private Button viewLayoutEditorBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, container, false);
        TVChannelBtn = view.findViewById(R.id.change_channel); //Change name of button
        musicChannelBtn = view.findViewById(R.id.change_music);
        viewLayoutEditorBtn = view.findViewById(R.id.change_layout);

        Log.d(TAG, "onCreateView: Started");

        Toast.makeText(getActivity(), "Going to TAG", Toast.LENGTH_SHORT).show();

        TVChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Going to ChangeTVStationFragment", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setViewPager(1);
            }
        });

        musicChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setViewPager(2);
            }
        });

        viewLayoutEditorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setViewPager(3);
            }
        });

        return view;
    }

}
