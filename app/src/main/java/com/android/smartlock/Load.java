package com.android.smartlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Handler handler= new Handler();
        handler.postDelayed((Runnable) () -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }, 4000);
    }
}