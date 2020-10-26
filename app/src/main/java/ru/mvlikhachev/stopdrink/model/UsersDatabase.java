package ru.mvlikhachev.stopdrink.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class UsersDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();

    private static UsersDatabase instance;

    public static synchronized UsersDatabase getInstance(Context context){

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    UsersDatabase.class, "UsersDB")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new InitialDataAsyncTask(instance).execute();
        }
    };

    private static class InitialDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserDao userDao;

        public InitialDataAsyncTask(UsersDatabase database) {
            userDao = database.getUserDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            User adminUser = new User();
            adminUser.setDbId(1);
            adminUser.setUid("1");
            adminUser.setEmail("admin");
            adminUser.setName("admin");

            return null;
        }
    }

}
