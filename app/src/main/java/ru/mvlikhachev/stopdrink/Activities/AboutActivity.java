package ru.mvlikhachev.stopdrink.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import ru.mvlikhachev.stopdrink.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Убрать ActionBar
        getSupportActionBar().hide();
        showRatingUserInterface(AboutActivity.this);
    }



    private void showRatingUserInterface(final Activity activity) {
        final ReviewManager manager = ReviewManagerFactory.create(activity);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // do nothing

                });
            }
        });
    }
}