package ru.mvlikhachev.stopdrink.database.Room

import androidx.lifecycle.LiveData
import ru.mvlikhachev.stopdrink.database.DatabaseRepository
import ru.mvlikhachev.stopdrink.model.User

class AppRoomRepository(private val appRoomDao: AppRoomDao) : DatabaseRepository {

    override val allNotes: LiveData<List<User>>
        get() = appRoomDao.getAllPersons()

    override suspend fun getPersonById(id: Int): LiveData<User> {
        val person = appRoomDao.getById(id)
        return person
    }

    override suspend fun insert(person: User, onSuccess: () -> Unit) {
        appRoomDao.insert(person)
        onSuccess()
    }

    override suspend fun delete(person: User, onSuccess: () -> Unit) {
        appRoomDao.delete(person)
        onSuccess()
    }

    override val isDBExists: Boolean
        get() = appRoomDao.isExists()


}