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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.DAO.Utils;


public class SettingActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";
///////////////////////////////////////////////////////////////////

    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;

    private FirebaseAuth auth;

    private TextInputLayout renameTextInputLayout;
    private TextInputEditText renameTextInputEditText;

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
    private String newDate;

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

        // Убрать ActionBar
        getSupportActionBar().hide();

        renameTextInputLayout = findViewById(R.id.renameTextInputLayout);
        renameTextInputEditText = findViewById(R.id.renameTextInputEditText);
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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                newYear = String.valueOf(year);
                newMonth = String.valueOf(month + 1);
                newDay= String.valueOf(dayOfMonth);
            }
        });

        renameTextInputEditText.setText(oldName);

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

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
        }
    };

    public void saveNewData(View view) {

        if (oldDate.equals(newDate) && oldName.equals(newName)) {
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
            finish();
        }
        if (Utils.hasConnection(this)) {
            updateData();
            //databaseReference.removeEventListener(valueEventListener);


            Log.d("setValue", "In saveNewData ID: " + userId);
            Toast.makeText(this, "Готово! ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void updateData() {
        // Set new name
        newName = renameTextInputLayout.getEditText().getText().toString();
        userDatabaseReference.child(userId).child("name").setValue(newName);

        //set new date
        long iMonth = Integer.parseInt(newMonth);
        long iDay = Integer.parseInt(newDay);

        if (iMonth < 10) {
            newMonth = "0" + iMonth;
        }
        if (iDay < 10) {
            newDay = "0" + iDay;
        }
        if (myHour < 10) {
            newHour = "0" + myHour;
        } else {
            newHour = String.valueOf(myHour);
        }
        if (myMinute < 10) {
            newMinute = "0" + myMinute;
        } else {
            newMinute = String.valueOf(myMinute);
        }

        newDate = newYear + "/" + newMonth + "/" + newDay + " " + newHour + ":"+ newMinute+":00";

        userDatabaseReference.child(userId).child("dateWhenStopDrink").setValue(newDate);
        goOfflineConnection();
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