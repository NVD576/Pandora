package com.example.pandora.Class;

public class Rating {
    int id;
    int userid;
    int restaurantid;
    int star=0;

    public Rating(int id, int userid, int restaurantid, int star) {
        this.id = id;
        this.userid = userid;
        this.restaurantid = restaurantid;
        this.star = star;
    }

    public Rating(int userid, int restaurantid, int star) {
        this.userid = userid;
        this.restaurantid = restaurantid;
        this.star = star;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getRestaurantid() {
        return restaurantid;
    }

    public void setRestaurantid(int restaurantid) {
        this.restaurantid = restaurantid;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
