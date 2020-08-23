package ru.mvlikhachev.stopdrink.Utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import ru.mvlikhachev.stopdrink.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "exampleChannel";
    public static final int NOTIFICATION_ID = 1;


    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "-______-", Toast.LENGTH_SHORT).show();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1);
    }

    // Create notification method
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // Show notification method
    public static void showNotification(Context context, String day) {
        NotificationManagerCompat notificationManager;
        RemoteViews collapsedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_expanded);
        Intent clickIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
                0, clickIntent, 0);
        collapsedView.setTextViewText(R.id.notificationHelloTextView, "Поздравляем!");
        collapsedView.setTextViewText(R.id.descriptionNotificationHelloTextView, "Вы не пьете - " + day + " дней");
        expandedView.setTextViewText(R.id.expandedDaysTextViw, "Не пью дней - " + day + "!");
        expandedView.setOnClickPendingIntent(R.id.expandedDaysTextViw, clickPendingIntent);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);
    }

    // Show review window
    public static void showRatingUserInterface(final Activity activity) {
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