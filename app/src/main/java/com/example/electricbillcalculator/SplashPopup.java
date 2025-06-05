package com.example.electricbillcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashPopup extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_popup);

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashPopup.this,Splash.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 4500);
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}