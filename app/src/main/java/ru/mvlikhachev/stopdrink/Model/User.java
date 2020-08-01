package ru.mvlikhachev.stopdrink.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String id;
    private String email;
    private String name;
    private String dateWhenStopDrink;
    private String test;

    public User() {
    }

    public User(String id, String name, String email, String test) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.test = test;
    }

    public User(String id, String email, String name, String dateWhenStopDrink, String test) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.test = test;
        this.dateWhenStopDrink = dateWhenStopDrink;
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

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("dateWhenStopDrink", dateWhenStopDrink);
        result.put("test", test);

        return result;
    }
}
