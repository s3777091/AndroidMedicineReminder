package com.dab.medireminder.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.dab.medireminder.R;
import com.dab.medireminder.constant.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);

        boolean isFirst = sharedPreferences.getBoolean(Constants.KEY_FIRST_OPEN_APP, false);
        if (isFirst) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btnStarted)
    public void onStartedMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
