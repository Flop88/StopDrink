package ru.mvlikhachev.stopdrink.Activities;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils;


public class SettingActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
///////////////////////////////////////////////////////////////////

    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;

    private FirebaseAuth auth;

    private TextInputLayout newNameTextInputLayout;

    private CalendarView calendarView;
    private TextView yearTextView;
    private TextView monthTextView;
    private TextView dayTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    String oldName;
    String newName;

    String oldDate;
    String newDate;

    String newYear;
    String newMonth;
    String newDay;

    String idKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Убрать ActionBar
        getSupportActionBar().hide();

        newNameTextInputLayout = findViewById(R.id.renameTextInputLayout);
        calendarView = findViewById(R.id.calendarView);
        yearTextView = findViewById(R.id.yearTextView);
        monthTextView = findViewById(R.id.monthTextView);
        dayTextView = findViewById(R.id.dayTextView);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");

        sharedPreferences = this.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        oldName = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                "Default Name");
        oldDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                "Default Name");


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                newYear = String.valueOf(year);
                newMonth = String.valueOf(month + 1);
                newDay= String.valueOf(dayOfMonth);

                yearTextView.setText(newYear);
                monthTextView.setText(newMonth);
                dayTextView.setText(newDay);
            }
        });


    }

    public void saveNewData(View view) {

        if (oldDate.equals(newDate) && oldName.equals(newName)) {
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
            finish();
        }
        if (Utils.hasConnection(this)) {
            getUserId();

            Log.d("setValue", "In saveNewData " + idKey);
            Toast.makeText(this, "Готово! ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
            finish();
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
                        idKey = key;
                        updateData(idKey);
                        Log.d("setValue", "In loop " + idKey);
                    }
                    break;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateData(String key) {

        long iMonth = Integer.parseInt(newMonth);
        long iDay = Integer.parseInt(newDay);

        newName = newNameTextInputLayout.getEditText().getText().toString();

        if (iMonth < 10) {
            newMonth = "0" + iMonth;
        }
        if (iDay < 10) {
            newDay = "0" + iDay;
        }

        newDate = newYear + "/" + newMonth + "/" + newDay + " 00:00:00";

        Log.d("setValue", newDate);
        userDatabaseReference.child(key).child("dateWhenStopDrink").setValue(newDate);

        Log.d("setValue", newName);
        userDatabaseReference.child(key).child("name").setValue(newName);


    }
    @Override
    protected void onResume()
    {
        super.onResume();

        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
    }
}