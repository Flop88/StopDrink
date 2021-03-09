package ru.mvlikhachev.stopdrink.screens.Room.MainScreen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import java.text.SimpleDateFormat
import java.util.*

class MainRoomFragmentViewModel(application: Application) : AndroidViewModel(application) {
    fun resetTimer() {
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
//        val currentDate = sdf.format(Date())
        val currentDate = getDate(System.currentTimeMillis(), "dd/MM/yyyy HH:mm:ss")
        with (sharedPref.edit()) {
            putString("userDataDate", currentDate)
            Log.d("resetTimer", "NewDate: $currentDate")
            apply()
        }
    }
    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}