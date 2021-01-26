package ru.mvlikhachev.stopdrink.screens.Room.MainScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main_room.*
import ru.mvlikhachev.stopdrink.R
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import ru.mvlikhachev.stopdrink.Utils.Utils.calculateTimeWithoutDrink


class MainRoomFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_room, container, false)
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val name = sharedPref.getString("userDataName", "NULL")
        val about = sharedPref.getString("userDataAbout", "NULL")
        val date = sharedPref.getString("userDataDate", "NULL")

        setText(name, date)
    }

    private fun setText(name: String?, date: String?) {
        val mDate = "2021/01/01 00:00:00"
        val newDate = calculateTimeWithoutDrink(mDate)

        Log.d("checkDate", "days: ${newDate[0]}")
        Log.d("checkDate", "minutes: ${newDate[1]}")
        Log.d("checkDate", "seconds: ${newDate[2]}")

        usernameTextView.text = "Здраствуйте, $name"
//        daysTextView.text = "${newDate[0]} дней"
//        timeTextView.text = "${newDate[1]}:${newDate[2]}"
    }

}