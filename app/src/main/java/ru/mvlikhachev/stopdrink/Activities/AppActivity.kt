package ru.mvlikhachev.stopdrink.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_app.*
import ru.mvlikhachev.stopdrink.Fragments.MainFragment
import ru.mvlikhachev.stopdrink.Fragments.ProfileFragment
import ru.mvlikhachev.stopdrink.Fragments.SettingsFragment
import ru.mvlikhachev.stopdrink.R
import kotlinx.android.synthetic.main.activity_main.*

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        val profileFragment = ProfileFragment()
        val mainFragment = MainFragment()
        val settingsFragment = SettingsFragment()


        Log.d("makeCurrentFragment", "Run app")

        makeCurrentFragment(mainFragment)



        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.profile_page -> makeCurrentFragment(profileFragment)
                R.id.main_page -> makeCurrentFragment(mainFragment)
                R.id.settings_page -> makeCurrentFragment(settingsFragment)
            }
            true
            }

    }

    private fun makeCurrentFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_wrapper, fragment)
                Log.d("makeCurrentFragment", "pum-pum-pum")
                commit()
            }
}