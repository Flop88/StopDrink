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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils.LoadReferences;
import ru.mvlikhachev.stopdrink.Utils.Utils;
import ru.mvlikhachev.stopdrink.Utils.NotificationReceiver;

import static ru.mvlikhachev.stopdrink.Utils.Utils.goOfflineConnectiontoDatabase;
import static ru.mvlikhachev.stopdrink.Utils.Utils.goOnlineConnectiontoDatabase;

public class MainActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_ABOUT_ME = "aboutMeFromDb";
    public static final String APP_PREFERENCES_KEY_PROFILE_IMAGE = "profileImageFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";
    ////////////////////// INITIALIZATION /////////////////////////////////
    private TextView helloUsernameTextView;
    private TextView daysTextView;
    private TextView timeTextView;
    private ImageView logoImageView;
    ///////////////////////// DATA ////////////////////////////////////
    private String username;
    private String lastDrinkDate;
    private String userId;
    private String daysWithoutDrink;
    ////////////////////////// FIREBASE ///////////////////////////////
    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener userChildeEventListener;
    private ChildEventListener loadDateUserChildeEventListener;
    private ChildEventListener aboutChildeEventListener;

    private FirebaseAuth auth;

    // AdMob
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    ///////////////////////////////////////////////////////////////////
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
///////////////////////////////////////////////////////////////////

    private NotificationManagerCompat notificationManager;


    // TEST

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

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
        userId = Utils.getUserId(this);
        daysWithoutDrink = "0";
//////// End initialization block

        // Если не авторизованы - идев в активити авторизации
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
        }

        goOnlineConnectiontoDatabase();
        if (Utils.hasConnection(this)) {
            // load name from firebase database
            getNameFromDatabase();

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

            NotificationReceiver.createNotificationChannel(this);
            NotificationReceiver.showNotification(this, daysWithoutDrink);

            return false;
        });
        showBottomNavigation(R.id.main_page);
    }

    // Show bottom navighation menu
    private void showBottomNavigation(int currentMenu) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(currentMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile_page:
                    startActivity(new Intent(getApplicationContext(),
                            ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.main_page:
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.settings_page:
                    startActivity(new Intent(getApplicationContext(),
                            SettingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    // AdMob show AD method
    private void showAdMob() {
        Log.d("AdMob", "AdMob run...");
        MobileAds.initialize(this, initializationStatus -> {
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
                    daysWithoutDrink = dates[0];
                    setNotDrinkTime(dates[0],dates[1],dates[2]);

                    // Show notification if hour = 00 and minute = 00
                    if (dates[1].equals("00") && dates[2].equals("00")) {
                        Utils.showNotificationWithDate(getApplicationContext(),dates[0]);
                    }


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
                Log.e("DANGEEEEER", "USER ID: " + user.getId());
                Log.e("DANGEEEEER","FIREBASE USER ID: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
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


    // Set date data in TextView
    private void setNotDrinkTime(String days, String hours, String minutes) {
        daysTextView.setText(days + " дней");
        timeTextView.setText(hours + ":" + minutes);
    }

    // Button "сбросить"
    public void resetDrinkDate(View view) {
        if (Utils.hasConnection(this)) {
            String id = Utils.getUserId(this);
            String updateDate = Utils.getCurrentDate();
                userDatabaseReference.child(id).child("dateWhenStopDrink").setValue(updateDate);

                String[] dates = Utils.calculateTimeWithoutDrink(updateDate);
                setNotDrinkTime(dates[0], dates[1], dates[2]);

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
                    Intent intent = new Intent(this, LoginSignUpActivity.class);
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


    @Override
    protected void onResume() {
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

    @Override
    protected void onStart() {
        super.onStart();

        LoadReferences.loadDataFromDbAndPutInSharedPreferences(this);

        String dbid = sharedPreferences.getString(APP_PREFERENCES_KEY_USERID,
                "ID");
        String dbName = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                "Default Name");
        String dbDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                "Default Name");
        String dbAbout = sharedPreferences.getString(APP_PREFERENCES_KEY_ABOUT_ME,
                "Default Name");
        String dbProfileImg = sharedPreferences.getString(APP_PREFERENCES_KEY_PROFILE_IMAGE,
                "Default Name");

    }
}