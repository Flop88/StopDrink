package ru.mvlikhachev.stopdrink.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    private ImageView vk;
    private ImageView insta;
    private ImageView telega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        vk = findViewById(R.id.vkImageView);
        insta = findViewById(R.id.instagramImageView);
        telega = findViewById(R.id.telegramImageView);

        // Убрать ActionBar
        getSupportActionBar().hide();

        vk.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/mv.likhachev"));
            startActivity(browserIntent);
        });

        insta.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mv.likhachev"));
            startActivity(browserIntent);
        });
        telega.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/mlikhachev"));
            startActivity(browserIntent);
        });
    }

    public void writeReview(View view) {
        showRatingUserInterface(AboutActivity.this);
    }
}