package com.example.pandora.Class;

public class Review {
    private int id;
    int restaurantid;
    private int userid;
    private String review;
    String date;

    public Review(int id,int userid, int restaurantid, String review, String date) {
        this.id = id;
        this.restaurantid = restaurantid;
        this.userid = userid;
        this.review = review;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Review(int userid, int restaurantid, String review, String date) {
        this.userid = userid;
        this.restaurantid = restaurantid;
        this.review = review;
        this.date = date;
    }

    public Review(int id, int userid, String review) {
        this.id = id;
        this.userid = userid;
        this.review = review;
    }

    public Review(int userid, String review) {
        this.userid = userid;
        this.review = review;
    }

    public int getRestaurantid() {
        return restaurantid;
    }

    public void setRestaurantid(int restaurantid) {
        this.restaurantid = restaurantid;
    }

    // Getters và setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
