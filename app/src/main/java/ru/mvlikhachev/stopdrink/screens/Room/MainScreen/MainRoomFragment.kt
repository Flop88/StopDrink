package ru.mvlikhachev.stopdrink.screens.Room.MainScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_main_room.*
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import ru.mvlikhachev.stopdrink.Utils.Utils.calculateTimeWithoutDrink
import ru.mvlikhachev.stopdrink.databinding.FragmentMainRoomBinding


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
                    sleep(5000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    setUsername(name)
                    setDate(date)
                }
            }
        }
        thread.start()

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
        val mDate = "$date 00:00:00" // 2021/01/01
        val newDate = calculateTimeWithoutDrink(mDate)

        daysTextView.text = "${newDate[0]} дней"
        timeTextView.text = "${newDate[1]}:${newDate[2]}"
    }

}