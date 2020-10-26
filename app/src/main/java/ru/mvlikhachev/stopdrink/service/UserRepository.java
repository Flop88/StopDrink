package ru.mvlikhachev.stopdrink.service;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import ru.mvlikhachev.stopdrink.model.User;
import ru.mvlikhachev.stopdrink.model.UserDao;
import ru.mvlikhachev.stopdrink.model.UsersDatabase;

public class UserRepository {

    private UserDao userDao;

    private LiveData<User> user;

    public UserRepository(Application application) {

            UsersDatabase database = UsersDatabase.getInstance(application);
            userDao = database.getUserDao();
        }

        public LiveData<User> getUserByUid(String uid) {
            return userDao.getUserByUid(uid);
        }



    // Insert
    public void insertUser(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;

        public InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
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
