package ru.mvlikhachev.stopdrink.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ru.mvlikhachev.stopdrink.Model.User;

public class Utils {
    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";
///////////////////////////////////////////////////////////////////


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
    public static String getUserId(Context context) {
        final String[] result = {""};
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReference = database.getReference().child("users");
        if (Utils.hasConnection(context)) {
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    goOnlineConnectiontoDatabase();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            String email = childSnapshot.child("email").getValue(String.class);

                            if (email.equals(auth.getCurrentUser().getEmail())) {
                                result[0] = key;
                                // Save "userId" on local storage

                                editor.putString(APP_PREFERENCES_KEY_USERID, result[0]);
                                editor.apply();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            result[0] =  sharedPreferences.getString(APP_PREFERENCES_KEY_USERID,
                    "qwerty");

            return result[0];
        } else {
            return  sharedPreferences.getString(APP_PREFERENCES_KEY_USERID,
                    "qwerty");
        }
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

    // Add drink date in db
    public static void addDrinkDate(Context context, String userId) {
//        HashSet<String> set = new HashSet<>();

        ArrayList<String> db = new ArrayList<>();
        ArrayList<String> set = new ArrayList<>();
        String date = getCurrentDate();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReference = database.getReference().child("users");
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ChildEventListener dataChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    ArrayList<String> dateList = user.getDrinksDate();

                    if(dateList != null) {
                        for (int i = 0; i < dateList.size(); ++i) {
                            set.add(dateList.get(i));
                        }
                    }

                    //userDatabaseReference.child(userId).child("drink_date").setValue(set.add(date));
                    Log.d("sett", "setInOnChildAdded: " + set);
                    Log.d("sett", "setInOnChildAdded: " + set.size());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        userDatabaseReference.addChildEventListener(dataChildeEventListener);

        set.add(date);
        userDatabaseReference.child(userId).child("drink_date").setValue(set);
        Log.d("sett", "set: " + set);
        Log.d("sett", "set: " + set.size());
    }
}