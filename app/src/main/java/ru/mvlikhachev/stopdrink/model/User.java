package ru.mvlikhachev.stopdrink.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Entity(tableName = "users_table")
public class User extends BaseObservable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String uid;
    private String email;
    private String name;
    private String dateWhenStopDrink;
    private String aboutMe;
    private String profileImage;

    public User() {
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public User(int id, String uid, String email, String name, String dateWhenStopDrink, String aboutMe, String profileImage, ArrayList<String> drinksDate) {

        this.id = id;
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.dateWhenStopDrink = dateWhenStopDrink;
        this.aboutMe = aboutMe;
        this.profileImage = profileImage;
    }

    public User(String dateWhenStopDrink) {
        this.dateWhenStopDrink = dateWhenStopDrink;
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDateWhenStopDrink() {
        return dateWhenStopDrink;
    }

    public void setDateWhenStopDrink(String dateWhenStopDrink) {
        this.dateWhenStopDrink = dateWhenStopDrink;
        notifyPropertyChanged(BR.dateWhenStopDrink);
    }


    @Bindable
    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
        notifyPropertyChanged(BR.aboutMe);
    }

    @Bindable
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        notifyPropertyChanged(BR.profileImage);
    }

    @Bindable
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        notifyPropertyChanged(BR.uid);
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("dateWhenStopDrink", dateWhenStopDrink);

        return result;
    }
}
