package com.gopi.lostandfound.services;

import java.io.Serializable;

public class Item implements Serializable {
    private long id; // Change from int to long
    private String type;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;

    public Item(String type, String name, String phone, String description, String date, String location) {
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    public long getId() { // Change from int to long
        return id;
    }

    public void setId(long id) { // Change from int to long
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
