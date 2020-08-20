package ru.mvlikhachev.stopdrink.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ru.mvlikhachev.stopdrink.R;

public class SplashActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        // Убрать ActionBar
        getSupportActionBar().hide();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Если авторизованы - запускаем MainActivity, если нет - LoginSignUpActivity
                    if (auth.getCurrentUser() != null) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else{
                        startActivity(new Intent(SplashActivity.this, LoginSignUpActivity.class));
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}