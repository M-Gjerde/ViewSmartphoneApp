package net.kaufmanndesigns.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SetupCalendarFragment extends Fragment {
    private static final String TAG = "SetupCalendarFragment";

    SendSetupData sendSetupData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + inflater.inflate(R.layout.setup_calendar_fragment, container, false));

        return inflater.inflate(R.layout.setup_calendar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView outlook = view.findViewById(R.id.outlook);
        TextView gmail = view.findViewById(R.id.gmail);
        TextView other = view.findViewById(R.id.other);


        outlook.setOnClickListener(view1 -> {
            sendSetupData.sendSetupData("outlook");
            sendSetupData.removeFragment();
        });
        gmail.setOnClickListener(view1 -> {
            sendSetupData.sendSetupData("gmail");
            sendSetupData.removeFragment();
        });
        other.setOnClickListener(view1 -> {
            sendSetupData.sendSetupData("zoho");
            sendSetupData.removeFragment();
        });
    }

    interface SendSetupData {
        void sendSetupData(String service_provider);

        void removeFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sendSetupData = (SendSetupData) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e);
            throw new ClassCastException(e.toString());
        }
    }
}
