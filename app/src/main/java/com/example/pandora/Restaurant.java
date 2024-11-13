package com.example.pandora;

public class Restaurant {


    private int id;
    private String name;
    private String review;
    private int image;
    private int start;

//    public Restaurant(int start, int image, String review, String name, int id) {
//        this.start = start;
//        this.image = image;
//        this.review = review;
//        this.name = name;
//        this.id = id;
//    }

    public Restaurant(String name, String review, int image, int start) {
        this.name = name;
        this.review = review;
        this.image = image;
        this.start = start;
    }


    // Constructor đầy đủ (có id)
    public Restaurant(int id, String name, String review, int imageResId) {
        this.id = id;
        this.name = name;
        this.review = review;
        this.image = imageResId;
    }

    // Constructor không có id
    public Restaurant(String name, String review, int imageResId) {
        this.name = name;
        this.review = review;
        this.image = imageResId;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getImageResId() {
        return image;
    }

    public void setImageResId(int imageResId) {
        this.image = imageResId;
    }
}
