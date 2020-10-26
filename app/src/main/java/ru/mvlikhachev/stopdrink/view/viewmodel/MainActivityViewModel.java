package ru.mvlikhachev.stopdrink.view.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.mvlikhachev.stopdrink.model.User;
import ru.mvlikhachev.stopdrink.service.UserRepository;

public class MainActivityViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private LiveData<User> user;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<User> getUser(String uid) {
        user = userRepository.getUserByUid(uid);
        return user;
    }

    public void addNewUser(User user) {
        userRepository.insertUser(user);
    }
    public void updateUser(User user) {
        userRepository.updatetUser(user);
    }
    public void deleteUser(User user) {
        userRepository.deletetUser(user);
    }
}
