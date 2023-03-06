package com.dab.medireminder.base;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dab.medireminder.R;
import com.dab.medireminder.utils.AppUtils;

import butterknife.ButterKnife;


//Create abstract baseActivity as know as template of application
//So while i create activity i only extends from abstract so i don't need set up anything
public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getLayout();

    public abstract void initView();

    public abstract void setEvents();

    public abstract void setData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        //Using ButterKnife library
        ButterKnife.bind(this);
        //Disable status bar
        AppUtils.setTransparentStatusBar(this);

        //Checking text to speech file
        //If not have return to 0 then download it
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 1);
        
        initView();
        setData();
        setEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //It will go to this lines a lot
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                Toast.makeText(getApplicationContext(), "Installed Now", Toast.LENGTH_LONG).show();
            }
        }
    }
}
