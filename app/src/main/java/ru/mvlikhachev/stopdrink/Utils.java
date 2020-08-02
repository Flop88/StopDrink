package ru.mvlikhachev.stopdrink;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {

    //////////////////////// Constants ////////////////////////////////
    // Константа файла сохранения настроек
    public static final String APP_PREFERENCES = "datasetting";
    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
///////////////////////////////////////////////////////////////////

///////////////////////// DATA ////////////////////////////////////
    private String username;
    private String lastDrinkDate;
///////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
///////////////////////////////////////////////////////////////////

////////////////////////// FIREBASE ///////////////////////////////
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabaseReference = database.getReference().child("users");

    private ChildEventListener userChildeEventListener;
    private ChildEventListener loadDateUserChildeEventListener;

    private FirebaseAuth auth = FirebaseAuth.getInstance();


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


}
