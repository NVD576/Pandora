package com.example.pandora.Class;

public class Review {
    private int id;
    int restaurantid;
    private String name;
    private String review;
    int rating;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Review(int id, String name, String review) {
        this.id = id;
        this.name = name;
        this.review = review;
    }

    public Review(String name, String review, int rating) {
        this.name = name;
        this.review = review;
        this.rating = rating;
    }

    public int getRestaurantid() {
        return restaurantid;
    }

    public void setRestaurantid(int restaurantid) {
        this.restaurantid = restaurantid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    // Getters và setters
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
