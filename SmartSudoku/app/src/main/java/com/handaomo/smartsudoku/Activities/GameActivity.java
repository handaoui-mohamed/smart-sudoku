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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handaomo.smartsudoku.api_services.Api;
import com.handaomo.smartsudoku.api_services.GamePreferences;
import com.handaomo.smartsudoku.dtos.GridDto;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.views.Grille;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {
    private static final int CHOICE_RESULT = 24;
    private boolean gameOver = false;
    private Grille grid;
    private int currentX;
    private int currentY;
    private TextView resultTxt, timerTxt;
    private Button submitBnt;
    private boolean newGame = true;
    long remainingTime = 0;
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
        submitBnt = findViewById(R.id.confirmBtn);
        resultTxt = findViewById(R.id.gameResult);
        timerTxt = findViewById(R.id.timerTxt);

        if (savedInstanceState == null) {
            // check if it's a new game
            Intent gameIntent = getIntent();
            newGame = gameIntent.getBooleanExtra("new_game", true);
            String gridFromNotif = gameIntent.getStringExtra("grid_config");

            resultTxt.setText(getString(R.string.loading_grid));
            submitBnt.setEnabled(false);

            if (newGame) {
                if (gridFromNotif.length() == 81) loadConfigFromNotif(gridFromNotif);
                else loadTodayConfig();
            } else loadSavedConfig();
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
                        if (gameOver) return false;
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();
                        if (!grid.isNotFix(currentX, currentY)) {
                            performClick();
                            return true;
                        }
                        return false;
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

    public void saveGame() {
        int matrix[][] = grid.getGameMatrix();
        boolean fixIdx[][] = grid.getFixIdx();

        StringBuilder gameConfig = new StringBuilder();
        StringBuilder fixedItems = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gameConfig.append(matrix[i][j]);
                fixedItems.append(fixIdx[i][j] ? 0 : 1);
            }
        }

        gamePreferences.saveGame(this, gameConfig + ":" + fixedItems, remainingTime);
    }

    private void loadConfigFromNotif(String game) {
        if (!game.equals("")) {
            gameOver = false;

            resultTxt.setText("");

            boolean fixIdx[][] = new boolean[9][9];

            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    fixIdx[i][j] = game.charAt((i * 9) + j) != '0';

            grid.applyNewConfig(game, fixIdx);
            submitBnt.setEnabled(true);

            startNewCountDownTimer();
            newGame = true;
        } else {
            loadNewGame();
        }
    }

    private void loadSavedConfig() {
        String game = gamePreferences.getSavedGame(this);

        if (!game.equals("")) {
            gameOver = false;

            resultTxt.setText("");
            String config[] = game.split(":");

            boolean fixIdx[][] = new boolean[9][9];

            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    fixIdx[i][j] = config[1].charAt((i * 9) + j) == '0';

            grid.applyNewConfig(config[0], fixIdx);
            submitBnt.setEnabled(true);

            startNewCountDownTimer();
            newGame = true;
        } else {
            loadNewGame();
        }
    }

    public void loadNewGame() {
        gameOver = false;
        loadTodayConfig();
        // restart countdown timer
        startNewCountDownTimer();
    }

    public void confirmSolution(View view) {
        countDownTimer.cancel();
        gameOver = true;
        boolean gameWon = grid.gagne();
        resultTxt.setText(gameWon ? getString(R.string.you_won) : getString(R.string.you_lost));
        grid.setGameState(gameWon ? 1 : 2);
    }

    public void loadTodayConfig() {
        gameOver = false;
        Api.gridService.getTodayGrid().enqueue(new Callback<GridDto>() {
            @Override
            public void onResponse(Call<GridDto> call, Response<GridDto> response) {
                setNewConfig(response);
            }

            @Override
            public void onFailure(Call<GridDto> call, Throwable t) {
                //TODO: show error message or retry
            }
        });
    }

    public void loadRandomConfig() {
        gameOver = false;
        Api.gridService.getRandomGrid().enqueue(new Callback<GridDto>() {
            @Override
            public void onResponse(Call<GridDto> call, Response<GridDto> response) {
                setNewConfig(response);
            }

            @Override
            public void onFailure(Call<GridDto> call, Throwable t) {
                //TODO: show error message or retry
            }
        });
    }

    private void setNewConfig(Response<GridDto> response) {
        resultTxt.setText("");
        grid.applyNewConfig(response.body().configuration);
        submitBnt.setEnabled(true);

        startNewCountDownTimer();
        newGame = true;
    }

    private void startNewCountDownTimer() {
        long countDownTime = gamePreferences.getCountDownTime(this, newGame);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(countDownTime, 1000) {
            public void onTick(long millis) {
                GameActivity.this.remainingTime = millis;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTxt.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                confirmSolution(null);
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
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.reloadGameOptionBtn:
                grid.setGameState(0);
                loadTodayConfig();
                startNewCountDownTimer();
                Toast.makeText(this, "Partie rechargée", Toast.LENGTH_LONG).show();
                return true;
            case R.id.randomGameOptionBtn:
                grid.setGameState(0);
                loadRandomConfig();
                startNewCountDownTimer();
                Toast.makeText(this, "Partie aléatoire chargée", Toast.LENGTH_LONG).show();
                return true;
            case R.id.saveOptionBtn:
                saveGame();
                Toast.makeText(this, "Partie sauvegardée", Toast.LENGTH_LONG).show();
                return true;
            case R.id.loadOptionBtn:
                grid.setGameState(0);
                loadSavedConfig();
                newGame = false;
                startNewCountDownTimer();
                newGame = true;
                Toast.makeText(this, "Partie chargée", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
