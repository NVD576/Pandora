package com.example.pandora.Class;

public class Favorite {
    int id;
    int restaurantid;
    int userid;
    int like=0;


    public Favorite(int id, int restaurantid, int userid, int like) {
        this.id = id;
        this.restaurantid = restaurantid;
        this.userid = userid;
        this.like = like;
    }

    public Favorite(int restaurantid, int userid, int like) {
        this.restaurantid = restaurantid;
        this.userid = userid;
        this.like = like;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantid() {
        return restaurantid;
    }

    public void setRestaurantid(int restaurantid) {
        this.restaurantid = restaurantid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
