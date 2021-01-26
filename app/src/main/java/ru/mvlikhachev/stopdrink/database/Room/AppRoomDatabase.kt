package ru.mvlikhachev.stopdrink.database.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mvlikhachev.stopdrink.model.User

@Database(entities = [User::class],version = 1)
abstract class AppRoomDatabase: RoomDatabase() {
    abstract fun getAppRoomDao():AppRoomDao

    companion object{

        @Volatile
        private var database:AppRoomDatabase?=null

        @Synchronized
        fun getInstance(context: Context):AppRoomDatabase{
            return if (database==null){
                database = Room.databaseBuilder(context, AppRoomDatabase::class.java, "database").build()
                database as AppRoomDatabase
            } else database as AppRoomDatabase
        }
    }
}
