package com.example.pandora.Class;

import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    String address;
    int locationid;
    int cateid;
    private String image;
    private int star;
    int listImageid;
    String description;
    int history =0;

    public Restaurant() {
    }

    public Restaurant(int id, String name, String address, int locationid, int cateid, String image, int star, int history) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationid = locationid;
        this.cateid = cateid;
        this.image = image;
        this.star = star;
        this.history = history;
    }

    public Restaurant(int id, String name, String address, int locationid, int cateid, String image, int star, String description, int history) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationid = locationid;
        this.cateid = cateid;
        this.image = image;
        this.star = star;
        this.description = description;
        this.history = history;
    }

    public Restaurant(String name, String address, int locationid, int cateid, String description) {
        this.name = name;
        this.address = address;
        this.locationid = locationid;
        this.cateid = cateid;
        this.description = description;
    }

    public Restaurant(String name, int locationid, int star) {
        this.name = name;
        this.locationid = locationid;
        this.star = star;
    }

    public Restaurant(String name, int locationid, int cateid, int star) {
        this.name = name;
        this.locationid = locationid;
        this.cateid = cateid;
        this.star = star;
    }

    public int getImageid() {
        return listImageid;
    }

    public void setImageid(int imageid) {
        this.listImageid = imageid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLocationid() {
        return locationid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public int getCateid() {
        return cateid;
    }

    public void setCateid(int cateid) {
        this.cateid = cateid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }


    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


