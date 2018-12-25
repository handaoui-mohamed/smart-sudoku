package com.handaomo.smartsudoku.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handaomo.smartsudoku.Activities.AboutActivity;
import com.handaomo.smartsudoku.Activities.LoginActivity;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.Services.GamePreferences;

public class MainFragment extends Fragment {
    private Context context;
    Button playBtn, aboutBtn, loginBtn, logoutBtn, loadBtn;
    TextView currentUserTxt;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        playBtn = view.findViewById(R.id.playBtn);
        loadBtn = view.findViewById(R.id.loadBtn);
        aboutBtn = view.findViewById(R.id.aboutBtn);
        loginBtn = view.findViewById(R.id.loginBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        currentUserTxt = view.findViewById(R.id.currentUserTxt);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GamePreferences.getInstance().removeCurrentUser(context);
                loginBtn.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.GONE);
                loadBtn.setVisibility(View.GONE);
                logoutBtn.setVisibility(View.GONE);
                currentUserTxt.setText(getString(R.string.not_loggedin));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startGame(true);
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startGame(false);
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

    private void startGame(boolean newGame) {
        GameFragment gameFragment = new GameFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("new_game", newGame);
        gameFragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, gameFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentUser = GamePreferences.getInstance().getCurrentUser(context);
        GameFragment.initialised = false;
        if(currentUser.equals("")){
            loginBtn.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
            loadBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            currentUserTxt.setText(getString(R.string.not_loggedin));
        }else {
            loginBtn.setVisibility(View.GONE);
            playBtn.setVisibility(View.VISIBLE);
            loadBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            currentUserTxt.setText(currentUser);
        }
    }
}
