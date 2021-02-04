package ru.mvlikhachev.stopdrink.screens.Room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.mvlikhachev.stopdrink.R
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import ru.mvlikhachev.stopdrink.databinding.ActivityRoomNavigationBinding

class RoomNavigationActivity : AppCompatActivity() {

    lateinit var mNavController: NavController
    private var _binding: ActivityRoomNavigationBinding? = null
    val mBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRoomNavigationBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        APP_ACTIVITY = this
        mNavController = Navigation.findNavController(this, R.id.nav_host)
    }
}