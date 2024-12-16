package com.example.pandora.Class;

public class Image {
    int id;
    int restaurantid;
    String imageUrl;

    public Image(int id, int restaurantid, String imageUrl) {
        this.id = id;
        this.restaurantid = restaurantid;
        this.imageUrl = imageUrl;
    }

    public Image(int restaurantid, String imageUrl) {
        this.restaurantid = restaurantid;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
