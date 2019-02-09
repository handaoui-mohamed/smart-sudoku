package com.handaomo.smartsudoku.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handaomo.smartsudoku.Config;
import com.handaomo.smartsudoku.api_services.GamePreferences;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.api_services.Api;
import com.handaomo.smartsudoku.services.GridNotificationService;
import com.handaomo.smartsudoku.services.SocketIoService;

public class MainActivity extends AppCompatActivity {
    Context context;
    Button playBtn, aboutBtn, loginBtn, logoutBtn, loadBtn;
    TextView currentUserTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playBtn = findViewById(R.id.playBtn);
        loadBtn = findViewById(R.id.loadBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        loginBtn = findViewById(R.id.loginBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        currentUserTxt = findViewById(R.id.currentUserTxt);

        if (savedInstanceState == null) {
            setCurrentUser();
            Api.init();

            Intent intent = getIntent();
            if (intent != null) {
                String grid_config = intent.getStringExtra("new_grid");
                if (grid_config != null && grid_config.length() == 81) {
                    Log.i("SOCKETSS", grid_config);
                    startGame(true, grid_config);
                }
            }
        }
    }

    private void startGame(boolean newGame, String gridFromNotif) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("new_game", newGame);
        gameIntent.putExtra("grid_config", gridFromNotif);
        startActivity(gameIntent);
    }

    public void goToLogin(View view) {
        Intent loginIntent = new Intent(context, LoginActivity.class);
        startActivityForResult(loginIntent, Config.LOGIN_RESULT);
    }

    public void logout(View view) {
        GamePreferences.getInstance().removeCurrentUser(context);

        GridNotificationService.stop(this);
        SocketIoService.stop(this);

        hideViews();
    }

    public void loadGame(View view) {
        startGame(false, " ");
    }

    public void startNewGame(View view) {
        startGame(true, " ");
    }

    public void goToAbout(View view) {
        Intent aboutIntent = new Intent(context, AboutActivity.class);
        startActivity(aboutIntent);
    }

    private void hideViews() {
        loginBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.GONE);
        loadBtn.setVisibility(View.GONE);
        logoutBtn.setVisibility(View.GONE);
        currentUserTxt.setText(getString(R.string.not_loggedin));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Config.LOGIN_RESULT) {
            if (resultCode == Activity.RESULT_OK) setCurrentUser();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String grid_config = intent.getStringExtra("new_grid");
            if (grid_config != null && grid_config.length() == 81)
                startGame(true, grid_config);
        }
    }

    public void setCurrentUser() {
        String currentUser = GamePreferences.getInstance().getCurrentUser(context);
        if (currentUser.equals("")) {
            hideViews();
        } else {
            loginBtn.setVisibility(View.GONE);
            playBtn.setVisibility(View.VISIBLE);
            loadBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            currentUserTxt.setText(currentUser);
            SocketIoService.start(this);
        }
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
