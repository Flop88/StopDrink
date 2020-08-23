package ru.mvlikhachev.stopdrink.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import ru.mvlikhachev.stopdrink.R;

import static ru.mvlikhachev.stopdrink.Utils.NotificationReceiver.showRatingUserInterface;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Убрать ActionBar
        getSupportActionBar().hide();
    }

    public void writeReview(View view) {
        showRatingUserInterface(AboutActivity.this);
    }
}