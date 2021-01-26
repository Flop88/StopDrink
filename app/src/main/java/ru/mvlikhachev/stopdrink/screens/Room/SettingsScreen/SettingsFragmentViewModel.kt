package ru.mvlikhachev.stopdrink.screens.Room.SettingsScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mvlikhachev.stopdrink.Utils.REPOSITORY
import ru.mvlikhachev.stopdrink.Utils.TYPE_ROOM
import ru.mvlikhachev.stopdrink.database.Room.AppRoomDatabase
import ru.mvlikhachev.stopdrink.database.Room.AppRoomRepository
import ru.mvlikhachev.stopdrink.model.User

class SettingsFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application
    lateinit var readUserData: LiveData<List<User>>

    fun initDatabase(type: String, onSuccess:() -> Unit) {
        when(type) {
            TYPE_ROOM -> {
                val dao = AppRoomDatabase.getInstance(mContext).getAppRoomDao()
                REPOSITORY = AppRoomRepository(dao)
                onSuccess()
            }
        }
    }

    fun insert(appPerson: User, onSuccess: () -> Unit) =
            viewModelScope.launch(Dispatchers.Main) {
                REPOSITORY.insert(appPerson) {
                    onSuccess()
                }
            }

}