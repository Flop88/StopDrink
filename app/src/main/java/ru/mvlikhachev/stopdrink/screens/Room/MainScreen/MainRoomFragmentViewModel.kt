package ru.mvlikhachev.stopdrink.screens.Room.MainScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import java.text.SimpleDateFormat
import java.util.*

class MainRoomFragmentViewModel(application: Application) : AndroidViewModel(application) {

    fun resetTimer() {
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        with (sharedPref.edit()) {
            putString("userDataDate", currentDate)
            apply()
        }
    }
}