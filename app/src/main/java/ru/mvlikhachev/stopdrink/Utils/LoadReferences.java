package ru.mvlikhachev.stopdrink.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.mvlikhachev.stopdrink.Model.User;

public class LoadReferences {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
    public static final String APP_PREFERENCES_KEY_ABOUT_ME = "aboutMeFromDb";
    public static final String APP_PREFERENCES_KEY_PROFILE_IMAGE = "profileImageFromDb";

    public static final int RC_IMAGE_PICER = 1488;


    public static void deleteInSharedPreferences(Context context) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReference = database.getReference().child("users");

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ChildEventListener dataChildeEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    // Save DATA on local storage
                    editor.putString(APP_PREFERENCES_KEY_NAME, " ");
                    editor.putString(APP_PREFERENCES_KEY_DATE, " ");
                    editor.putString(APP_PREFERENCES_KEY_ABOUT_ME, " ");
                    editor.putString(APP_PREFERENCES_KEY_PROFILE_IMAGE, " ");
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

        userDatabaseReference.addChildEventListener(dataChildeEventListener);
    }
}
