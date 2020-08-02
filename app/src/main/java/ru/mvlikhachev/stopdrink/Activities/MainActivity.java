package ru.mvlikhachev.stopdrink.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;

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

///////////////////////// DATA ////////////////////////////////////
    private String username;
    private String lastDrinkDate;
///////////////////////////////////////////////////////////////////

///////////////////////// Threads /////////////////////////////////
    private Thread thread;
///////////////////////////////////////////////////////////////////

////////////////////////// FIREBASE ///////////////////////////////
    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener userChildeEventListener;
    private ChildEventListener loadDateUserChildeEventListener;

    private FirebaseAuth auth;
///////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
///////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        daysTextView = findViewById(R.id.daysTextView);
        timeTextView = findViewById(R.id.timeTextView);
        resetTimeButton = findViewById(R.id.resetTimeButton);

        sharedPreferences = this.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");

        Intent intent = getIntent();
        if(intent != null) {
            username = intent.getStringExtra("userName");
            lastDrinkDate = intent.getStringExtra("drinkDate");
        }



        if (hasConnection(this)) {
            // load name from firebase database
            getNameFromDatabase();
            // load last date when user drink alcohol from firebase database
            getDateOfLastDrinkFromDatabase();

        } else {
            username = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                    "Default Name");
            lastDrinkDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                    "2000/01/01 00:00:00");
        }

        //Поток запуска расчета времени
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUserame(username);
                                calculateTime(lastDrinkDate);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    // Set username in TextView
    private void setUserame(String username) {
        helloUsernameTextView.setText("Здраствуйте, " + username);
    }

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

    // Update Date method
    private void updateDate(String s) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        userDatabaseReference.child(s).child("dateWhenStopDrink").setValue(dateFormat.format(date));
    }

    // Get date when user last drink alcohol from firebase database method
    private void getDateOfLastDrinkFromDatabase() {
        loadDateUserChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    lastDrinkDate = user.getDateWhenStopDrink();

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
                finish();
                return true;
            case R.id.settings_programm:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Button "Сорвался"
    public void resetDrinkDate(View view) {
        
        if (hasConnection(this)) {
            getUserId();
            getDateOfLastDrinkFromDatabase();
        }

    }

    // Метод получает ID и email текущего пользователя Firebase realtime database, сравнивает с
    // емейлом авторизованного пользователя и если они сходятся - вызыввает метод updateDate() в который передает ID
    private void getUserId() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("users");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String key = dataSnapshot1.getKey();
                    String email = dataSnapshot1.child("email").getValue(String.class);

                    if (email.equals(auth.getCurrentUser().getEmail())) {
                        updateDate(key);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}