package ru.mvlikhachev.stopdrink.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils.Utils;
import ru.mvlikhachev.stopdrink.Utils.Validations;


public class SettingActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_ABOUT_ME = "aboutMeFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";
///////////////////////////////////////////////////////////////////

    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener aboutChildeEventListener;

    private FirebaseAuth auth;

    private TextInputLayout renameTextInputLayout;
    private TextInputLayout aboutTextInputLayout;
    private TextInputEditText renameTextInputEditText;
    private TextInputEditText aboutTextInputEditText;

    private CalendarView calendarView;

    private TextView testTextView;
    int DIALOG_TIME = 1;
    int myHour = 14;
    int myMinute = 35;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String oldName;
    private String newName;

    private String oldDate;
    private String textAboutMe;

    private String newYear;
    private String newMonth;
    private String newDay;
    private String newHour;
    private String newMinute;


    private String userId;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        renameTextInputLayout = findViewById(R.id.renameTextInputLayout);
        aboutTextInputLayout = findViewById(R.id.aboutTextInputLayout);
        renameTextInputEditText = findViewById(R.id.renameTextInputEditText);
        aboutTextInputEditText = findViewById(R.id.aboutTextInputEditText);
        calendarView = findViewById(R.id.calendarView);

        testTextView = findViewById(R.id.tvTime);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");
        goOnlineConnection();

        sharedPreferences = this.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        oldName = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                "Default Name");
        oldDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                "Default Name");
        userId = sharedPreferences.getString(APP_PREFERENCES_KEY_USERID,
                "qwerty");
        textAboutMe = sharedPreferences.getString(APP_PREFERENCES_KEY_ABOUT_ME,
                "Simple text");

        Log.d("settingActivityData", "oldName: " + oldName);
        Log.d("settingActivityData", "oldDate: " + oldDate);
        Log.d("settingActivityData", "about: " + textAboutMe);
        Log.d("settingActivityData", "userId: " + userId);

        // Устанавливаем текущую дату максимальным числом в календаре
        Calendar calendar = Calendar.getInstance();
        long currentDate = calendar.getTimeInMillis();
        calendarView.setMaxDate(currentDate);


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            newYear = String.valueOf(year);
            newMonth = String.valueOf(month + 1);
            newDay= String.valueOf(dayOfMonth);
        });

        renameTextInputEditText.setText(oldName);
        aboutTextInputEditText.setText(textAboutMe);

        showBottomNavigation(R.id.settings_page);

    }


    // Get text about me from firebase database method
    private void getNameFromDatabase() {
        aboutChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    textAboutMe = user.getAboutMe();

                    // Save "username" on local storage
                    editor.putString(APP_PREFERENCES_KEY_ABOUT_ME, textAboutMe);
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

        userDatabaseReference.addChildEventListener(aboutChildeEventListener);
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

    public void onclick(View view) {
        showDialog(DIALOG_TIME);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = (view, hourOfDay, minute) -> {
        myHour = hourOfDay;
        myMinute = minute;
        newHour = String.valueOf(myHour);
        newMinute = String.valueOf(myMinute);
    };

    public void saveNewData(View view) {

        if (Utils.hasConnection(this)) {

            // если поле не прошло валидацию - выводим ошибку
            if(!Validations.validateName(renameTextInputLayout)) {
                return;
            } else {
                if (newYear == null) {
                    newYear = "2020";
                }
                if (newMonth == null) {
                    newMonth = "01";
                }
                if (newDay == null) {
                    newDay = "01";
                }
                if (newHour == null) {
                    newHour = "01";
                }
                if (newMinute == null) {
                    newMinute = "01";
                }
                String setdate = setNewDataInDb(
                        Integer.parseInt(newYear),
                        Integer.parseInt(newMonth),
                        Integer.parseInt(newDay),
                        Integer.parseInt(newHour),
                        Integer.parseInt(newMinute)
                );

                Log.d("setDATA", "setdate: " + setdate);

                setNewNameInDb(oldName);
                setTextAboutMeInDb(textAboutMe);
                updateNewDataInDb(setdate);
                //databaseReference.removeEventListener(valueEventListener);
                Toast.makeText(this, "Готово! ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    private void updateNewDataInDb(String date) {
        userDatabaseReference.child(userId).child("dateWhenStopDrink").setValue(date);

        editor.putString(APP_PREFERENCES_KEY_DATE, date);
        editor.apply();
    }

    private String setNewDataInDb(int year, int month, int day, int hour, int minute) {
        //set new date
        String updateYear = "0";
        String updateMonth = "0";
        String updateDay = "0";
        String updateHour = "0";
        String updateMinute = "0";
        String upDate = "0";

        if (year == 0 || month == 0 || day == 0 || hour == 0 || minute == 0) {
            year = 2020;
            month = 01;
            day = 01;
            hour = 01;
            minute = 01;
        }

        updateYear = String.valueOf(year);

        if (month < 10) {
            updateMonth = "0" + month;
        } else {
            updateMonth = String.valueOf(month);
        }
        if (day < 10) {
            updateDay = "0" + day;
        } else {
            updateDay = String.valueOf(day);
        }
        if (hour < 10) {
            updateHour = "0" + hour;
        } else {
            updateHour = String.valueOf(hour);
        }
        if (minute < 10) {
            updateMinute = "0" + minute;
        } else {
            updateMinute = String.valueOf(minute);
        }
        upDate = updateYear + "/" + updateMonth + "/" + updateDay + " " + updateHour + ":"+ updateMinute+":00";
        return upDate;
    }

    private void setNewNameInDb(String name) {
        // Set new name
        name = renameTextInputLayout.getEditText().getText().toString();
        userDatabaseReference.child(userId).child("name").setValue(name);

        editor.putString(APP_PREFERENCES_KEY_NAME, name);
        editor.apply();
    }

    private void setTextAboutMeInDb(String textAboutMe) {
        // Set new text about me
        textAboutMe = aboutTextInputLayout.getEditText().getText().toString();
        userDatabaseReference.child(userId).child("aboutMe").setValue(textAboutMe);

        editor.putString(APP_PREFERENCES_KEY_ABOUT_ME, textAboutMe);
        editor.apply();
    }

    private void goOnlineConnection() {
        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }
    }

    private void goOfflineConnection() {
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        goOnlineConnection();
    }


    @Override
    public void onPause() {

        super.onPause();

        goOfflineConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        goOfflineConnection();
    }
}