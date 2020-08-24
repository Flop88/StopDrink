package ru.mvlikhachev.stopdrink.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.Date;

import ru.mvlikhachev.stopdrink.Fragments.MainFragment;
import ru.mvlikhachev.stopdrink.Fragments.ProfileFragment;
import ru.mvlikhachev.stopdrink.Fragments.SettingsFragment;
import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.DAO.Utils;

public class MainActivity extends AppCompatActivity {

//////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
///////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////
    private TextView helloUsernameTextView;
    private TextView daysTextView;
    private TextView timeTextView;
    private Button resetTimeButton;
///////////////////////////////////////////////////////////////////
    private Fragment profileFragment;
    private Fragment mainFragment;
    private Fragment settingsFragment;

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
        resetTimeButton = findViewById(R.id.resetTimeButton);

        sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");


        username = "";
        lastDrinkDate = "2000/01/01 00:00:00";
        userId = "";
//////// End initialization block

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


        showBottomNavigation(R.id.main_page);

    }

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
                    calculateTime(lastDrinkDate);

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

    // Метот расчитывает время с даты последнего употребления алкоголя до текущего момента
    private void calculateTime(String date) {
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

        daysTextView.setText(diffDays + " дней");
        timeTextView.setText(hoursString + ":" + minutesString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
                finish();
                return true;
            case R.id.about_program:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            case R.id.settings_programm:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Button "Сорвался"
    public void resetDrinkDate(View view) {
        if (Utils.hasConnection(this)) {
             updateDateAndTimeInFirebaseDatabase();
             getDateOfLastDrinkFromDatabase();
        }
    }

    // Метод получает ID и email текущего пользователя Firebase realtime database, сравнивает с
    // емейлом авторизованного пользователя и если они сходятся - обновляем дату употребления на сервере
    private void updateDateAndTimeInFirebaseDatabase() {
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goOnlineConnectiontoDatabase();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        String email = childSnapshot.child("email").getValue(String.class);

                        Log.d("keyS", "Value: " + key);
                        if (email.equals(auth.getCurrentUser().getEmail())) {
                            userId = key;
                        }
                    }
                    Log.d("keyS", userId);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    lastDrinkDate = dateFormat.format(date);
                    if (date.toString() != lastDrinkDate) {
                        userDatabaseReference.child(userId).child("dateWhenStopDrink").setValue(dateFormat.format(date));
                    }

                } else {
                    // code if data does not  exists
                    Log.d("keyS", "---------------------------");
                    Log.d("keyS", "ELSE BLOCK RUN!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        mAdView.resume();

        goOnlineConnectiontoDatabase();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAGG", "The interstitial wasn't loaded yet.");
        }
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