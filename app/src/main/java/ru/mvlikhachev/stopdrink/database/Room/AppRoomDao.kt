package ru.mvlikhachev.stopdrink.database.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.mvlikhachev.stopdrink.model.User

@Dao
interface AppRoomDao {

    @Query("SELECT * FROM users_table")
    fun getAllPersons() : LiveData<List<User>>

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getById(id: Int) : LiveData<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: User)

    @Delete
    suspend fun delete(note: User)

    @Query("SELECT EXISTS(SELECT * FROM users_table)")
    fun isExists(): Boolean
}