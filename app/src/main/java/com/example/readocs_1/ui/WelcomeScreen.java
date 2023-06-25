package com.example.readocs_1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readocs_1.MainActivity;
import com.example.readocs_1.R;

public class WelcomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_900)); //Установление цвета панели навигации

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeScreen.this, MainActivity.class));
                finish();
            }
        }, 1100);
    }
}
