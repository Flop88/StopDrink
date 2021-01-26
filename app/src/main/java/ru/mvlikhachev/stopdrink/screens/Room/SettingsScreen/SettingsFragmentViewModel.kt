package ru.mvlikhachev.stopdrink.screens.Room.SettingsScreen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY

class SettingsFragmentViewModel(application: Application) : AndroidViewModel(application) {

    fun saveData(name: String, about: String, date: String) {
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("userDataName", name)
            putString("userDataAbout", about)
            putString("userDataDate", date)
            apply()
        }
    }

    fun testLoadData() {
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val name = sharedPref.getString("userDataName", "NULL")
        val about = sharedPref.getString("userDataAbout", "NULL")
        val date = sharedPref.getString("userDataDate", "NULL")
        Log.d("loadDate", "Name: $name" )
        Log.d("loadDate", "About: $about" )
        Log.d("loadDate", "Date: $date" )
    }
}