package ru.mvlikhachev.stopdrink.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import ru.mvlikhachev.stopdrink.R
import ru.mvlikhachev.stopdrink.Utils.Utils


class ProfileActivity : AppCompatActivity() {

    //////////////////////// Constants ////////////////////////////////////
    // Константа файла сохранения настроек
    val APP_PREFERENCES = "datasetting"
    val APP_PREFERENCES_KEY_NAME = "nameFromDb"
    val APP_PREFERENCES_KEY_DATE = "dateFromDb"
    val APP_PREFERENCES_KEY_USERID = "userIdFromDb"

    val WEEK_DATE = 7
    val MONTH_DATE = 30
    val HALFYEAR_DATE = 180

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //SharedPreferences
        val sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE
        )

        val username = sharedPreferences.getString(APP_PREFERENCES_KEY_NAME,
                "Default Name")
        val oldDate = sharedPreferences.getString(APP_PREFERENCES_KEY_DATE,
                "128")
        val userId = sharedPreferences.getString(APP_PREFERENCES_KEY_USERID,
                "qwerty")
        val date = Utils.calculateTimeWithoutDrink(oldDate)
        val daysWithoutDrink = date[0]
        val weekWithoutDrink = daysWithoutDrink.toFloat()
        val monthWithoutDrink = daysWithoutDrink.toFloat()
        val halfYearWithoutDrink = daysWithoutDrink.toFloat()

        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.apply {

            if (oldDate != null) {
                setProgressWithAnimation(daysWithoutDrink.toFloat(), 1000) // =1s
            }
        }

        // Week ProgressBar
        val weekCircularProgressBar = findViewById<CircularProgressBar>(R.id.weekCircularProgressBar)
        weekCircularProgressBar.apply {
            if (weekWithoutDrink <= WEEK_DATE) {
                setProgressWithAnimation(weekWithoutDrink, 1000)
            } else {
                setProgressWithAnimation(WEEK_DATE.toFloat(), 1000)
            }
        }

        // Month ProgressBar halfYearWithoutDrink
        val monthCircularProgressBar = findViewById<CircularProgressBar>(R.id.monthCircularProgressBar)
        monthCircularProgressBar.apply {
            if (monthWithoutDrink <= MONTH_DATE) {
                setProgressWithAnimation(weekWithoutDrink, 1000)
            } else {
                setProgressWithAnimation(MONTH_DATE.toFloat(), 1000)
            }
        }

        // Month halfYearWithoutDrink
        val halfYearCircularProgressBar = findViewById<CircularProgressBar>(R.id.monthCircularProgressBar)
        halfYearCircularProgressBar.apply {
            if (halfYearWithoutDrink <= HALFYEAR_DATE) {
                setProgressWithAnimation(halfYearWithoutDrink, 1000)
            } else {
                setProgressWithAnimation(HALFYEAR_DATE.toFloat(), 1000)
            }
        }

        setDataOnTextView(username, R.id.profileNameTextView)
        setDataOnTextView("Days without alohole - $daysWithoutDrink", R.id.profileAboutTextView)
        setDataOnTextView(daysWithoutDrink, R.id.daysTextInProgressBarTextView)

        showBottomNavigation(R.id.profile_page)
    }

    private fun setDataOnTextView(text: String?, textView: Int) {
        val textView: TextView = findViewById<TextView>(textView)
        textView.setText(text)
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
        menuInflater.inflate(R.menu.up_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.settings_programm -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        // хз зачем, но без нее не работает
        return super.onOptionsItemSelected(item)
    }
}