package ru.mvlikhachev.stopdrink.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    // Проверка подключения к интернету
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    // Получить текущую дату
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    // Метот расчитывает время с даты последнего употребления алкоголя до текущего момента
    public static String[] calculateTimeWithoutDrink(String date) {
        String[] result = {"10", "00", "00"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        long timeUp = 0;
        try {
            timeUp = format.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = System.currentTimeMillis() - timeUp;

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        // Проверка если минуты и секунды меньше 10 - выполняем форматирование, чтоб красиво отображалось во вью
        String hoursString = "";
        String minutesString = "";
        String daysString = String.valueOf(diffDays);

        if(diffHours < 10) {
            hoursString = "0" + diffHours;
        } else {
            hoursString = String.valueOf(diffHours);
        }

        if(diffMinutes < 10) {
            minutesString = "0" + diffMinutes;
        } else {
            minutesString = String.valueOf(diffMinutes);
        }
        result[0] = daysString;
        result[1] = hoursString;
        result[2] = minutesString;

        return result;
    }

    public static void goOfflineConnectiontoDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (database != null) {
            database.goOffline();
        }
    }
    public static void goOnlineConnectiontoDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (database != null) {
            database.goOnline();
        }
    }

    // Получаем user id из Firebase и присваиваем его в userId и помещаем в APP_PREFERENCES_KEY_USERID
    public static String getUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        return firebaseUser.getUid();
    }


    // Show notification method
    public static void showNotificationWithDate(Context context, String date) {
        switch (date) {
            case "7":
            case "14":
            case "21":
            case "50":
            case "100":
            case "150":
            case "200":
            case "250":
            case "300":
            case "365":
                NotificationReceiver.createNotificationChannel(context);
                NotificationReceiver.showNotification(context, date);
                break;
        }
    }
}