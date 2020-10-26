package ru.mvlikhachev.stopdrink.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("select * from users_table")
    LiveData<User> getAllUsers();


    @Query("select * from users_table where uid ==:userUid")
    LiveData<User> getUserByUid(int userUid);
}
