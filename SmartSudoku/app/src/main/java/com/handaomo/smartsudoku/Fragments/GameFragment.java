package com.handaomo.smartsudoku.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handaomo.smartsudoku.ApiServices.Api;
import com.handaomo.smartsudoku.ApiServices.GamePreferences;
import com.handaomo.smartsudoku.DTO.GridDto;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.Views.Grille;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameFragment extends Fragment {
    static boolean initialised = false;
    private Grille grid;
    private int currentX;
    private int currentY;
    private TextView resultTxt, timerTxt;
    private boolean newGame = true;
    CountDownTimer countDownTimer;
    GamePreferences gamePreferences;

    public GameFragment() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        gamePreferences = GamePreferences.getInstance();

        String currentUser = gamePreferences.getCurrentUser(getContext());
        ((TextView) view.findViewById(R.id.currentUserTxt)).setText(currentUser);

        grid = view.findViewById(R.id.sudoku_grid);
        grid.requestLayout();

        resultTxt = view.findViewById(R.id.gameResult);
        timerTxt = view.findViewById(R.id.timerTxt);


        if (!initialised) {
            newGame = getArguments().getBoolean("new_game", true);
            if (newGame) {
                loadNewConfig();
            } else {
                loadSavedConfig();
            }
            startNewCountDownTimer();
            resultTxt.setText(getString(R.string.loading_grid));
            initialised = true;
        }

        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        Button reloadBtn = view.findViewById(R.id.reloadBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                resultTxt.setText(grid.gagne() ? "You won!" : "You lost!!");
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewConfig();
                startNewCountDownTimer();
            }
        });

        grid.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();
                        if (!grid.isNotFix(currentX, currentY)) performClick();
                        return true;
                }
                return false;
            }

            private void performClick() {
                Bundle bundle = new Bundle();
                bundle.putInt("selected", grid.getElementFromMatrix(currentX, currentY));
                ChoiceFragment choiceFragment = new ChoiceFragment();
                choiceFragment.setParentFragment(GameFragment.this);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, choiceFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loadSavedConfig() {
        String game = gamePreferences.getSavedGame(getContext());
        if (!game.equals("")) {
            resultTxt.setText("");
            grid.applyNewConfig(game);
        }
    }

    public void loadNewConfig() {
        Api.gridService.getRandomGrid().enqueue(new Callback<GridDto>() {
            @Override
            public void onResponse(Call<GridDto> call, Response<GridDto> response) {
                resultTxt.setText("");
                grid.applyNewConfig(response.body().configuration);
            }

            @Override
            public void onFailure(Call<GridDto> call, Throwable t) {

            }
        });
    }

    public void setSelectedElement(int selectedElement) {
        grid.set(currentX, currentY, selectedElement);
    }

    private void startNewCountDownTimer() {
        long remainingTime = gamePreferences.getCountDownTime(getContext(), newGame);

        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(remainingTime, 1000) {
            public void onTick(long millis) {
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTxt.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                resultTxt.setText(grid.gagne() ? "You won!" : "You lost!!");
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Grille.spacing = gamePreferences.getGridSpacing(getContext());
    }
}
