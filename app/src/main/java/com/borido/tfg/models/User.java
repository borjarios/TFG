package com.borido.tfg.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String id;
    private String username;
    private String email;
    private boolean admin;
    private String image;
    private ArrayList<String> reserves;

    public User(String id, String username, String email, boolean admin, String image, ArrayList<String> reserves) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.admin = admin;
        this.image = image;
        this.reserves = reserves;
    }

    public User() {
    }

    public ArrayList<String> getReserves() {
        return reserves;
    }

    public void setReserves(ArrayList<String> reserves) {
        this.reserves = reserves;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", admin=" + admin +
                ", image='" + image + '\'' +
                ", reserves=" + reserves +
                '}';
    }

    /*------------------------------------------ OTHERS METHODS --------------------------------------------------*/

    public void update(User u){
        if(this.id == u.id){
            this.id = u.id;
            this.username = u.username;
            this.email = u.email;
            this.admin = u.admin;
            this.image = u.image;
            this.reserves = u.reserves;
        }
    }
}
