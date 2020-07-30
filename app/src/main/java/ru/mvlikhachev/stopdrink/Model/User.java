package ru.mvlikhachev.stopdrink.Model;

public class User {

    private String id;
    private String email;
    private String name;
    private String dateWhenStopDrink;

    public User() {
    }

    public User(String id, String email, String name, String dateWhenStopDrink) {
        this.id = id;
        this.email = email;
        this.name = name;
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
}
