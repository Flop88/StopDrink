package ru.mvlikhachev.stopdrink.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
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

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    // AdMob
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    ///////////////////////////////////////////////////////////////////
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
///////////////////////////////////////////////////////////////////

    private NotificationManagerCompat notificationManager;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        daysTextView = findViewById(R.id.daysTextView);
        timeTextView = findViewById(R.id.timeTextView);
        logoImageView = findViewById(R.id.logoImageView);

        sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");

//////// End initialization block

        // Если не авторизованы - идев в активити авторизации
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
        }

        if (Utils.hasConnection(this)) {
            // load last date when user drink alcohol from firebase database
            Thread updateDateThread = new Thread(() -> {
                while(true){
                    try {
                        Thread.sleep(60000); //1000 - 1 сек
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
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
    }
}