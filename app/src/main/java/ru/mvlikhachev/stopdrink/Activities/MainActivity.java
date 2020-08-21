package ru.mvlikhachev.stopdrink.Activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils.Utils;
import ru.mvlikhachev.stopdrink.Utils.NotificationReceiver;

public class MainActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";

    public static final String CHANNEL_ID = "exampleChannel";
    public static final int NOTIFICATION_ID = 1;
///////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////
    private TextView helloUsernameTextView;
    private TextView daysTextView;
    private TextView timeTextView;
    private ImageView logoImageView;
///////////////////////////////////////////////////////////////////

    ///////////////////////// DATA ////////////////////////////////////
    private String username;
    private String lastDrinkDate;
    private String userId;
///////////////////////////////////////////////////////////////////

    ////////////////////////// FIREBASE ///////////////////////////////
    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener userChildeEventListener;
    private ChildEventListener loadDateUserChildeEventListener;

    private FirebaseAuth auth;

    // AdMob
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
///////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
///////////////////////////////////////////////////////////////////

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AdMob
        showAdMob();

///////// Initialization block
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        daysTextView = findViewById(R.id.daysTextView);
        timeTextView = findViewById(R.id.timeTextView);
        logoImageView = findViewById(R.id.logoImageView);

        sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");


        username = "";
        lastDrinkDate = "2000/01/01 00:00:00";
        userId = getUserId();
//////// End initialization block

        // Если не авторизованы - идев в активити авторизации
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
        }

        goOnlineConnectiontoDatabase();
        if (Utils.hasConnection(this)) {
            // load name from firebase database
            getNameFromDatabase();

            Log.d("mainActivityData", "User ID: " + userId);
            Log.d("mainActivityData", "User NAME: " + username + "\n");
            // load last date when user drink alcohol from firebase database
            Thread updateDateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            getDateOfLastDrinkFromDatabase();
                            Thread.sleep(60000); //1000 - 1 сек
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            updateDateThread.start();

        } else {
            username = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                    "Default Name");
            lastDrinkDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                    "2000/01/01 00:00:00");
            Toast.makeText(this, "Для работы приложения нужен доступ в интернет", Toast.LENGTH_LONG).show();
        }
        notificationManager = NotificationManagerCompat.from(this);

        logoImageView.setOnLongClickListener(v -> {

            createNotificationChannel();
            showNotification();
            return false;
    });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void showNotification() {
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);
        Intent clickIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                0, clickIntent, 0);
        collapsedView.setTextViewText(R.id.text_view_collapsed_1, "Hello World!");
        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.logo);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();
        notificationManager.notify(1, notification);
    }


    private void showAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);

        mAdView = findViewById(R.id.adViewBottom);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D831A2241D7E1E3B316D46B94FAEE642")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3120800894638034/9851550730");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }


    // Get date when user last drink alcohol from firebase database method
    private void getDateOfLastDrinkFromDatabase() {
        loadDateUserChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    lastDrinkDate = user.getDateWhenStopDrink();
                    String[] dates = Utils.calculateTimeWithoutDrink(lastDrinkDate);
                    setNotDrinkTime(dates[0],dates[1],dates[2]);

                    // Save "username" on local storage
                    editor.putString(APP_PREFERENCES_KEY_DATE, lastDrinkDate);
                    editor.apply();
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
        userDatabaseReference.addChildEventListener(loadDateUserChildeEventListener);
    }

    // Get name from firebase database method
    private void getNameFromDatabase() {
        userChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    username = user.getName();
                    helloUsernameTextView.setText("Здраствуйте, " + username);

                    // Save "username" on local storage
                    editor.putString(APP_PREFERENCES_KEY_NAME, username);
                    editor.apply();
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

        userDatabaseReference.addChildEventListener(userChildeEventListener);
    }

    // Получаем user id из Firebase и присваиваем его в userId и помещаем в APP_PREFERENCES_KEY_USERID
    private String getUserId() {
        final String[] result = {""};
        if (Utils.hasConnection(this)) {
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

    private void setNotDrinkTime(String days, String hours, String minutes) {
        daysTextView.setText(days + " дней");
        timeTextView.setText(hours + ":" + minutes);
    }

    // Button "Сорвался"
    public void resetDrinkDate(View view) {
        if (Utils.hasConnection(this)) {
            String id = getUserId();
            String updateDate = Utils.getCurrentDate();
           // Log.d("resetDrink", "вставим дату - " + updateDate);
            userDatabaseReference.child(id).child("dateWhenStopDrink").setValue(updateDate);

            String[] dates = Utils.calculateTimeWithoutDrink(updateDate);
            //Log.d("resetDrink", "и установим новые значения - день:" + dates[0] + " часы:" + dates[1] + " минуты:" + dates[2]);
            setNotDrinkTime(dates[0],dates[1],dates[2]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Если пользователь авторизован - сразу открыть мэйн активити
        if (auth.getCurrentUser() != null) {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    Intent intent = new Intent(MainActivity.this, LoginSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    FirebaseAuth.getInstance().signOut();
                    return true;
                case R.id.about_program:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    return true;
                case R.id.settings_programm:
                    Intent intentSettings = new Intent(MainActivity.this, SettingActivity.class);
                    intentSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentSettings);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item); // хз зачем, но без нее не работает
    }































    // Метод получает ID и email текущего пользователя Firebase realtime database, сравнивает с
    // емейлом авторизованного пользователя и если они сходятся - обновляем дату употребления на сервере
    private void updateDateAndTimeInFirebaseDatabase() {
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goOnlineConnectiontoDatabase();
                if (dataSnapshot.exists()) {

                    lastDrinkDate = Utils.getCurrentDate();
                    String dateFirebase = Utils.getCurrentDate();

                        userDatabaseReference.child(userId).child("dateWhenStopDrink").setValue(dateFirebase);
                        editor.putString(APP_PREFERENCES_KEY_DATE, dateFirebase);
                        editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void goOfflineConnectiontoDatabase() {
        if (database != null) {
            database.goOffline();
        }
    }
    private void goOnlineConnectiontoDatabase() {
        if (database != null) {
            database.goOnline();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mAdView.resume();

        getDateOfLastDrinkFromDatabase();
        goOnlineConnectiontoDatabase();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAGG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void onPause() {
        mAdView.pause();

        super.onPause();

        goOfflineConnectiontoDatabase();
    }


    @Override
    protected void onDestroy() {
        mAdView.destroy();

        goOfflineConnectiontoDatabase();

        super.onDestroy();
    }


}