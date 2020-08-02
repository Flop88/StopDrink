package ru.mvlikhachev.stopdrink.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
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

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    String oldName;
    String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        newNameTextInputLayout = findViewById(R.id.renameTextInputLayout);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");

        sharedPreferences = this.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        oldName = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                "Default Name");

        newName = "";

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
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateName(String key) {
        userDatabaseReference.child(key).child("name").setValue(newNameTextInputLayout.getEditText().getText().toString());

    }
}