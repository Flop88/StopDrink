package ru.mvlikhachev.stopdrink.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;
import ru.mvlikhachev.stopdrink.Utils.LoadReferences;
import ru.mvlikhachev.stopdrink.Utils.Utils;
import ru.mvlikhachev.stopdrink.Utils.Validations;


public class SettingActivity extends AppCompatActivity {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_ABOUT_ME = "aboutMeFromDb";
    public static final String APP_PREFERENCES_KEY_PROFILE_IMAGE = "profileImageFromDb";
    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";

    public static final int RC_IMAGE_PICER = 1488;
///////////////////////////////////////////////////////////////////

    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener aboutChildeEventListener;

    private FirebaseAuth auth;

    private TextInputLayout renameTextInputLayout;
    private TextInputLayout aboutTextInputLayout;
    private TextInputEditText renameTextInputEditText;
    private TextInputEditText aboutTextInputEditText;

    private ImageView addImageButtonImageView;

    private CalendarView calendarView;

    private TextView testTextView;
    int DIALOG_TIME = 1;
    int myHour = 14;
    int myMinute = 35;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference profileImagesStorageReference;

    private String oldName;
    private String newName;

    private String oldDate;
    private String textAboutMe;

    private String realId;

    private String newYear;
    private String newMonth;
    private String newDay;
    private String newHour;
    private String newMinute;


    private String userId;
    private String urlProfileImg;

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
        addImageButtonImageView = findViewById(R.id.addImageButtonImageView);

        testTextView = findViewById(R.id.tvTime);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        User currentUser = new User();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference().child("users");
        profileImagesStorageReference = storage.getReference().child("avatars");
        goOnlineConnection();

        sharedPreferences = this.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        // Если не авторизованы - идев в активити авторизации
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(SettingActivity.this, LoginSignUpActivity.class));
        }


        String dbName = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME, "Username");
        String dbDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE, "2020/01/01 01:01:00");
        String dbAbout = sharedPreferences.getString(APP_PREFERENCES_KEY_ABOUT_ME, "Text about me");
        String dbProfileImg = sharedPreferences.getString(APP_PREFERENCES_KEY_PROFILE_IMAGE, "йцу");
        String dbId = sharedPreferences.getString(APP_PREFERENCES_KEY_USERID, "qweqweqweqwe");

        oldName = dbName;
        oldDate = dbDate;
        userId = dbId;
        textAboutMe = dbAbout;
        urlProfileImg = dbProfileImg;


        Log.d("settingActivityData", "oldName: " + oldName);
        Log.d("settingActivityData", "oldDate: " + oldDate);
        Log.d("settingActivityData", "userId: " + userId);
        Log.d("settingActivityData", "textAboutMe: " + textAboutMe);
        Log.d("settingActivityData", "urlProfileImg: " + urlProfileImg);

        realId = sharedPreferences.getString(APP_PREFERENCES_KEY_USERID, "qwe");
        Log.d("settingActivityData", "ID: " + realId);

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



        addImageButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Выберите картинку"),
                        RC_IMAGE_PICER);
            }
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


                updateDataInDb(realId, oldName, setdate);
//                setNewNameInDb(oldName);
//                setTextAboutMeInDb(textAboutMe);
//                updateNewDataInDb(setdate);
                //databaseReference.removeEventListener(valueEventListener);

                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }


    private String setNewDataInDb(int year, int month, int day, int hour, int minute) {
        //set new date
        String updateYear = "0";
        String updateMonth = "0";
        String updateDay = "0";
        String updateHour = "0";
        String updateMinute = "0";
        String upDate = "0";

        if (year == 0 ) {
            year = 2020;
        } else if (month == 0 ) {
            month = 01;
        } else if (day == 0 ) {
            day = 01;
        }else if ( hour == 0) {
            hour = 0;
        }else if (minute == 0) {
            minute = 0;
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

    private void updateDataInDb(String id, String name, String date) {
        // Set new name
        User currentUser = new User();

        name = renameTextInputLayout.getEditText().getText().toString();
        String aboutMe = aboutTextInputLayout.getEditText().getText().toString();
        String profileImage = sharedPreferences.getString(APP_PREFERENCES_KEY_PROFILE_IMAGE,
                "https://pixabay.com/ru/images/download/man-303792_640.png");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (Utils.hasConnection(this)) {

            currentUser.setId(firebaseUser.getUid());
            currentUser.setEmail(firebaseUser.getEmail());
            currentUser.setName(name);
            currentUser.setAboutMe(aboutMe);
            currentUser.setDateWhenStopDrink(date);
//        currentUser.setAvatarMockUpResource(0);
            currentUser.setProfileImage(urlProfileImg);
            if (id.length() == 20  && firebaseUser.getUid() != id) {
                userDatabaseReference.child(id).setValue(currentUser);
            } else {
                Toast.makeText(this, "Update ERROR", Toast.LENGTH_SHORT).show();
            }
        }


        editor.putString(APP_PREFERENCES_KEY_NAME, name);
        editor.putString(APP_PREFERENCES_KEY_ABOUT_ME, aboutMe);
        editor.putString(APP_PREFERENCES_KEY_DATE, date);
        editor.putString(APP_PREFERENCES_KEY_PROFILE_IMAGE, profileImage);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference imageReference = profileImagesStorageReference
                    .child(userId + "_" +selectedImageUri.getLastPathSegment());
            Log.d("UriImage", "" + imageReference);

            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        urlProfileImg = downloadUri.toString();

                        editor.putString(APP_PREFERENCES_KEY_PROFILE_IMAGE, urlProfileImg);
                        editor.apply();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

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
//
//        LoadReferences.deleteInSharedPreferences(this);
        goOfflineConnection();
    }
}