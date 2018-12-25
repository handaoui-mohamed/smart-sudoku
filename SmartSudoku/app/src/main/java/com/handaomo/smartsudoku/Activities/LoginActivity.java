package com.handaomo.smartsudoku.Activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.handaomo.smartsudoku.Config;
import com.handaomo.smartsudoku.DTO.UserDto;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.Services.Api;
import com.handaomo.smartsudoku.Services.GamePreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Context context;
    TextView responseTxt;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        Button registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        responseTxt = findViewById(R.id.responseTxt);
        responseTxt.setText("");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.usernameTxt)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordTxt)).getText().toString();

                tryToLogin(username, password);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.registerURL));
                startActivity(browserIntent);
            }
        });

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void tryToLogin(String username, String password) {
        responseTxt.setText(getString(R.string.logging_in));
        loginBtn.setEnabled(false);
        Api.userService.login(new UserDto("handaoui", "password")).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                UserDto user = response.body();
                GamePreferences.getInstance().setCurrentUser(context, user.first_name + " "+user.last_name );
                responseTxt.setText(getString(R.string.login_success));
                responseTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                finish();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                loginBtn.setEnabled(true);
                responseTxt.setText(getString(R.string.login_failed));
                responseTxt.setTextColor(getResources().getColor(R.color.red));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

