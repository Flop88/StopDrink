package ru.mvlikhachev.stopdrink.screens.Room.MainScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_main_room.*
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import ru.mvlikhachev.stopdrink.databinding.FragmentMainRoomBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainRoomFragment : Fragment() {

    private var _binding: FragmentMainRoomBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mViewModel: MainRoomFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainRoomBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }



    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(MainRoomFragmentViewModel::class.java)

        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val name = sharedPref.getString("userDataName", "NULL")
        val date = sharedPref.getString("userDataDate", "NULL")


        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    setUsername(name)
                    setDate(date)
                }
            }
        }
        thread.start()

        val updateDateThread = Thread {
            while (true) {
                try {
                    val date = sharedPref.getString("userDataDate", "NULL")
                    setDate(date)
                    Thread.sleep(60000) //1000 - 1 сек
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
        }
        updateDateThread.start()

        resetTimeRoomButton.setOnClickListener {
            mViewModel.resetTimer()
            val date = sharedPref.getString("userDataDate", "NULL")
            setDate(date)
        }
    }

    private fun setUsername(name: String?) {
        usernameTextView.text = "Здраствуйте, $name"
    }

    private fun setDate(date: String?) {
        try {
            Log.d("setDate", "Прилетела дата: $date")
            // создаем формат, в котором будем парсить дату
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date1 = dateFormat.parse("$date 00:00:00")
            val date2 = dateFormat.parse(getDate(System.currentTimeMillis(), "dd/MM/yyyy HH:mm:ss"))
            println("Первая дата: $date1")
            println("Вторая дата: $date2")
            val milliseconds = date2.time - date1.time


            val diffMinutes: Long = milliseconds / (60 * 1000) % 60
            val diffHours: Long = milliseconds / (60 * 60 * 1000) % 24
            val diffDays: Long = milliseconds / (24 * 60 * 60 * 1000)

            // Проверка если минуты и секунды меньше 10 - выполняем форматирование, чтоб красиво отображалось во вью
            var hoursString = ""
            var minutesString = ""
            val daysString = diffDays.toString()

            if (diffHours < 10) {
                hoursString = "0$diffHours"
            } else {
                hoursString = diffHours.toString()
            }

            if (diffMinutes < 10) {
                minutesString = "0$diffMinutes"
            } else {
                minutesString = diffMinutes.toString()
            }

            daysTextView.text = "${daysString} дней"
            timeTextView.text = "${hoursString}:${minutesString}"

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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