package ru.mvlikhachev.stopdrink.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.mvlikhachev.stopdrink.R
import ru.mvlikhachev.stopdrink.screens.Room.RoomNavigationActivity

class ChooseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)


    }

    fun startRoomMode(view: View) {
        startActivity(Intent(applicationContext,
                RoomNavigationActivity::class.java))
    }
    fun startFirebaseMode(view: View) {
        startActivity(Intent(applicationContext,
                LoginSignUpActivity::class.java))
    }
}