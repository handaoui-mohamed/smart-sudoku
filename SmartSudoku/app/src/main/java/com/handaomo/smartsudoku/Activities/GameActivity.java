package com.handaomo.smartsudoku.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.handaomo.smartsudoku.services.Api;
import com.handaomo.smartsudoku.services.GamePreferences;
import com.handaomo.smartsudoku.dtos.GridDto;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.views.Grille;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {
    private static final int CHOICE_RESULT = 24;
    private Grille grid;
    private int currentX;
    private int currentY;
    private TextView resultTxt, timerTxt;
    private boolean newGame = true;
    CountDownTimer countDownTimer;
    GamePreferences gamePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Game preferences
        gamePreferences = GamePreferences.getInstance();

        // current user
        String currentUser = gamePreferences.getCurrentUser(this);
        ((TextView) findViewById(R.id.currentUserTxt)).setText(currentUser);

        grid = findViewById(R.id.sudoku_grid);

        resultTxt = findViewById(R.id.gameResult);
        timerTxt = findViewById(R.id.timerTxt);

        if (savedInstanceState == null) {
            // check if it's a new game
            newGame = getIntent().getBooleanExtra("new_game", true);

            if (newGame) {
                loadNewConfig();
            } else {
                loadSavedConfig();
            }
            startNewCountDownTimer();
            resultTxt.setText(getString(R.string.loading_grid));
        }

        setGridTouchListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setGridTouchListener() {
        grid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        // TODO: check if the click is in the grid
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();
                        if (!grid.isNotFix(currentX, currentY)) performClick();
                        return true;
                }
                return false;
            }

            private void performClick() {
                Intent choiceIntent = new Intent(GameActivity.this, ChoiceActivity.class);
                choiceIntent.putExtra("selected", grid.getElementFromMatrix(currentX, currentY));
                startActivityForResult(choiceIntent, CHOICE_RESULT);
            }
        });
    }

    public void loadNewGame(View view) {
        loadNewConfig();
        // restart countdown timer
        startNewCountDownTimer();
    }

    public void confirmSolution(View view) {
        countDownTimer.cancel();
        resultTxt.setText(grid.gagne() ? "You won!" : "You lost!!");
    }

    private void loadSavedConfig() {
        String game = gamePreferences.getSavedGame(this);
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
                //TODO: show error message or retry
            }
        });
    }

    private void startNewCountDownTimer() {
        long remainingTime = gamePreferences.getCountDownTime(this, newGame);

        if (countDownTimer != null) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOICE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                grid.set(currentX, currentY, data.getIntExtra("choice", 0));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO : reload in a better way
        grid.setSpacing(gamePreferences.getGridSpacing(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
