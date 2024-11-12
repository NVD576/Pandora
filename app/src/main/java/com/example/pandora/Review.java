package com.example.pandora;

public class Review {
    private int id;
    private String name;
    private String review;
    private int imageResId;

    public Review(int id, String name, String review, int imageResId) {
        this.id = id;
        this.name = name;
        this.review = review;
        this.imageResId = imageResId;
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

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
