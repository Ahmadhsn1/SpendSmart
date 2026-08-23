package com.spendsmart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spendsmart.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // OOP: Lambda expression for Handler
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;

            if (user != null) {
                // Already logged in - go to main
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Not logged in - go to login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        }, SPLASH_DELAY);
    }
}
