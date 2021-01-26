package ru.mvlikhachev.stopdrink.database

import androidx.lifecycle.LiveData
import ru.mvlikhachev.stopdrink.model.User

interface DatabaseRepository {

    val allNotes: LiveData<List<User>>

    suspend fun getPersonById(id: Int) : LiveData<User>

    suspend fun insert(person: User, onSuccess:() -> Unit)
    suspend fun delete(person: User, onSuccess:() -> Unit)

    val isDBExists : Boolean

}