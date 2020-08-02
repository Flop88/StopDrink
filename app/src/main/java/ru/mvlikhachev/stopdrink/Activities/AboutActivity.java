package ru.mvlikhachev.stopdrink.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.mvlikhachev.stopdrink.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Убрать ActionBar
        getSupportActionBar().hide();
    }
}