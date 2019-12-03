package net.kaufmanndesigns.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kaufmanndesigns.view.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    FragmentActivity fragmentActivity;
    EditText password;
    EditText mirrorID;
    Button login;
    SharedPreferences sharedPref;

    SharedPreferences.Editor prefEditor;
    NameViewModel nameViewModel;
    sendLatestPreferencesToUI sendLatestPreferencesToUI;

    Observer<JSONObject> nameObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        login = view.findViewById(R.id.log_in);
        Context context = getContext();
        password = view.findViewById(R.id.password);
        mirrorID = view.findViewById(R.id.mirror_id);
        this.fragmentActivity = getActivity();

        PreferencesViewModel preferencesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PreferencesViewModel.class);         //PreferencesViewModel for database operations
        nameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NameViewModel.class);                       //Instantiate NameViewModel Class used for passing Strings from RSS feed to this fragment

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        prefEditor = sharedPref.edit();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preferencesViewModel.sendLoginRequest(mirrorID.getText().toString(), password.getText().toString(), nameViewModel);

                HashMap<String, String> params = new HashMap<>();
                params.put("getPreferences", "true");
                params.put("mirror_id", mirrorID.getText().toString());
                preferencesViewModel.sendJSONRequest(params, nameViewModel);

                prefEditor.putString(Constants.KEY_PASSWORD, password.getText().toString());
                prefEditor.putString(Constants.KEY_MIRROR_ID, mirrorID.getText().toString().trim());
                prefEditor.apply();
                //TODO Switch over to LayoutFragment if login was successful.
            }
        });


        // Create the observer which updates the UI.
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                Log.d(TAG, "onChanged: nameObserver " + newName);
                if (newName != null && newName.trim().equals("login_success")) {
                    Log.d(TAG, "onChanged: " + prefEditor);
                    prefEditor.putBoolean(Constants.KEY_LOGGED_IN, true);
                    prefEditor.apply();
                    updateUIFromLatestPreferences();
                    ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(0);
                } else {
                    Toast.makeText(getActivity(), "Fejl med log in " + newName, Toast.LENGTH_LONG).show();
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        nameViewModel.getLoginResponse().observe(this, nameObserver);
        return view;
    }

    void updateUIFromLatestPreferences() {
        Observer<JSONObject> nameObserver = new Observer<JSONObject>() {
            @Override
            public void onChanged(@Nullable JSONObject jsonObject) {
                Log.d(TAG, "onChanged: " + jsonObject.toString());
                sendLatestPreferencesToUI.sendLatestPreferences(jsonObject);
            }
        };

        nameViewModel.getJSONResponse().observe(this, nameObserver);
    }

    void removeLoginObserver() {
        //nameViewModel.getJSONResponse().removeObserver();
    }

    interface sendLatestPreferencesToUI {
        void sendLatestPreferences(JSONObject jsonObject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sendLatestPreferencesToUI = (LoginFragment.sendLatestPreferencesToUI) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e);
            throw new ClassCastException(e.toString());
        }
    }


}
