package com.example.pandora.Class;

public class Restaurant {
    private int id;
    private String name;
    String address;
    int locationid;
    int cateid;
    private int star;
    String description;

    public Restaurant() {
    }

    public Restaurant(int id, String name, String address, int locationid, int cateid, int star) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationid = locationid;
        this.cateid = cateid;
        this.star = star;
    }

    public Restaurant(int id, String name, String address, int locationid, int cateid,  int star, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationid = locationid;
        this.cateid = cateid;
        this.star = star;
        this.description = description;
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



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


