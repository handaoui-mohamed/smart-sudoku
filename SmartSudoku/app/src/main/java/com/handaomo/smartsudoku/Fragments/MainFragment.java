package com.handaomo.smartsudoku.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handaomo.smartsudoku.Activities.AboutActivity;
import com.handaomo.smartsudoku.Fragments.GameFragment;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.Services.GamePreferences;

public class MainFragment extends Fragment {
    private Context context;
    Button playBtn, aboutBtn, loginBtn, logoutBtn;
    TextView currentUserTxt;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        playBtn = view.findViewById(R.id.playBtn);
        aboutBtn = view.findViewById(R.id.aboutBtn);
        loginBtn = view.findViewById(R.id.loginBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        currentUserTxt = view.findViewById(R.id.currentUserTxt);

        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GameFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent aboutIntent = new Intent(context, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentUser = GamePreferences.getInstance().getCurrentUser(context);
        if(currentUser.equals("")){
            loginBtn.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            currentUserTxt.setText(getString(R.string.not_loggedin));
        }else {
            loginBtn.setVisibility(View.GONE);
            playBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            currentUserTxt.setText("Utilisateur : "+currentUser);
        }
    }
}
