package ru.mvlikhachev.stopdrink.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String id;
    private String email;
    private String name;
    private String dateWhenStopDrink;
    private String aboutMe;
    private String profileImage;
    private ArrayList<String> drinksDate;

    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public User(String id, String email, String name, String dateWhenStopDrink, String aboutMe, String profileImage, ArrayList<String> drinksDate, int avatarMockUpResource) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.dateWhenStopDrink = dateWhenStopDrink;
        this.aboutMe = aboutMe;
        this.profileImage = profileImage;
        this.drinksDate = drinksDate;
    }

    public User(String dateWhenStopDrink) {
        this.dateWhenStopDrink = dateWhenStopDrink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateWhenStopDrink() {
        return dateWhenStopDrink;
    }

    public void setDateWhenStopDrink(String dateWhenStopDrink) {
        this.dateWhenStopDrink = dateWhenStopDrink;
    }


    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<String> getDrinksDate() {
        return drinksDate;
    }

    public void setDrinksDate(ArrayList<String> drinksDate) {
        this.drinksDate = drinksDate;
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
