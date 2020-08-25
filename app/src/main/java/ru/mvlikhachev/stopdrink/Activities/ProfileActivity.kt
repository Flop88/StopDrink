package ru.mvlikhachev.stopdrink.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import ru.mvlikhachev.stopdrink.R


class ProfileActivity : AppCompatActivity() {

    //////////////////////// Constants ////////////////////////////////////
    // Константа файла сохранения настроек
    val APP_PREFERENCES = "datasetting"
    val APP_PREFERENCES_KEY_NAME = "nameFromDb"
    val APP_PREFERENCES_KEY_DATE = "dateFromDb"
    val APP_PREFERENCES_KEY_USERID = "userIdFromDb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //SharedPreferences
        val sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        val oldDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                "128")

        // Progress bar
        Log.d("datee","" +  oldDate?.toFloat())
        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.apply {
            // Set Progress
//            progress = 65f
            // or with animation
//            if (oldDate != null) {

                setProgressWithAnimation(65f, 1000)
//            } // =1s

        }


        showBottomNavigation(R.id.profile_page)
    }


    //Create bottom menu
    private fun showBottomNavigation(currentMenu: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = currentMenu
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.profile_page -> {
                    startActivity(Intent(applicationContext,
                            ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.main_page -> {
                    startActivity(Intent(applicationContext,
                            MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings_page -> {
                    startActivity(Intent(applicationContext,
                            SettingActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    // Create UP-menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val customView = (TextView)
        menuInflater.inflate(R.menu.up_navigation, menu)
//        val params: ActionBar.LayoutParams = ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER)

//        customView.setText("Some centered text");
//        getSupportActionBar()?.setCustomView(customView, params);
        return true
    }
}