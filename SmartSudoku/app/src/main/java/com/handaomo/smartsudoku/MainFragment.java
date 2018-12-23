package com.handaomo.smartsudoku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {
    private Context context;

    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button playBtn = view.findViewById(R.id.playBtn);
        Button aboutBtn = view.findViewById(R.id.aboutBtn);

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
}
