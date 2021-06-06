package com.borido.tfg.models;

import java.io.Serializable;

public class Library implements Serializable {

    private String id;
    private String name;
    private String direction;
    private String tel;
    private String schedule;
    private String location;
    private String latitude;
    private String longitude;
    private String image;

    public Library(String id, String name, String direction, String tel, String schedule, String location, String latitude, String longitude, String image) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.tel = tel;
        this.schedule = schedule;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public Library() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

    /*------------------------------------------ OTHERS METHODS --------------------------------------------------*/



    public void update(Library l){
        if(this.id == l.id){
            this.id = l.id;
            this.name = l.name;
            this.direction = l.direction;
            this.tel = l.tel;
            this.schedule = l.schedule;
            this.location = l.location;
            this.latitude = l.latitude;
            this.longitude = l.longitude;
            this.image = l.image;
        }
    }
}
