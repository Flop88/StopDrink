package ru.mvlikhachev.stopdrink.service;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.mvlikhachev.stopdrink.model.User;
import ru.mvlikhachev.stopdrink.model.UserDao;
import ru.mvlikhachev.stopdrink.model.UsersDatabase;

public class UserRepository {

    private UserDao userDao;

    private LiveData<User> user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;

    public UserRepository(Application application) {

            UsersDatabase database = UsersDatabase.getInstance(application);
            userDao = database.getUserDao();

            firebaseDatabase = FirebaseDatabase.getInstance();
            usersDatabaseReference = firebaseDatabase.getReference().child("users");
        }

        public LiveData<User> getUserByUid(String uid) {
            return userDao.getUserByUid(uid);
        }



    // Insert
    public void insertUser(User user) {
        new InsertUserAsyncTask(userDao, usersDatabaseReference).execute(user);
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;
        DatabaseReference usersDatabaseReference;

        public InsertUserAsyncTask(UserDao userDao, DatabaseReference usersDatabaseReference) {
            this.userDao = userDao;
            this.usersDatabaseReference = usersDatabaseReference;
        }

        @Override
        protected Void doInBackground(User... users) {
            // Add to Room
            userDao.insert(users[0]);
            Log.d("getUid", "\n UserRepo + InsertUserAsyncTask: ");
            Log.d("getUid", "Пользователь зарегестрирован и в несен в БД: ");
            Log.d("getUid", "UID: " + users[0].getUid());
            Log.d("getUid", "Email: " + users[0].getEmail());
            Log.d("getUid", "Name: " + users[0].getName());
            Log.d("getUid", "DATE: " + users[0].getDateWhenStopDrink());

            // Add to Firebase Database
            usersDatabaseReference.child(users[0].getUid()).setValue(users[0]);
            Log.d("getUid", "Все это дело улетело в firebase");
            return null;
        }
    }

    // Update
    public void updatetUser(User user) {
        new UpdateUserAsyncTask(userDao).execute(user);
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;

        public UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    // Delete
    public void deletetUser(User user) {
        new DeleteUserAsyncTask(userDao).execute(user);
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;

        public DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

}
