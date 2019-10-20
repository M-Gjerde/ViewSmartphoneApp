package net.kaufmanndesigns.view;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;


public class ChangeTVStationFragment extends Fragment {
    private static final String TAG = "ChangeTVStationFragment";

    SendMessage sendMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tv_stream_fragment, container, false);

        CardView channeltv2 = view.findViewById(R.id.channel_tv2);
        CardView channel_information = view.findViewById(R.id.channel_information);

        channeltv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData("TV2", "https://tv2lorry.dk/rss");
                ((MainActivity)getActivity()).setViewPager(0);
            }
        });

        channel_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage.sendData("Information.dk", "https://www.information.dk/feed");
                ((MainActivity)getActivity()).setViewPager(0);
            }
        });

        return view;
    }

    interface SendMessage{
        void sendData(String channel, String url);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sendMessage = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e);
            throw new ClassCastException(e.toString());
        }
    }
}
