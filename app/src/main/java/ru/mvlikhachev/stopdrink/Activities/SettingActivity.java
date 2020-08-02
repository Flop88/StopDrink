package ru.mvlikhachev.stopdrink.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
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
    private TextInputEditText nameTextInputEditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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

        if (hasConnection(this)) {
            getUserId();
            Toast.makeText(this, "Готово! ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
            finish();
        }
    }


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
                        updateName(key);
                        updateDate(key);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateDate(String key) {

        long iMonth = Integer.parseInt(newMonth);
        long iDay = Integer.parseInt(newDay);

        if (iMonth < 10) {
            newMonth = "0" + iMonth;
        }
        if (iDay < 10) {
            newDay = "0" + iDay;
        }

        newDate = newYear + "/" + newMonth + "/" + newDay + " 00:00:00";

        Log.d("setValue", newDate);
        userDatabaseReference.child(key).child("dateWhenStopDrink").setValue(newDate);

    }

    private void updateName(String key) {
        newName = newNameTextInputLayout.getEditText().getText().toString();

        Log.d("setValue", newName);
        userDatabaseReference.child(key).child("name").setValue(newName);
    }
}