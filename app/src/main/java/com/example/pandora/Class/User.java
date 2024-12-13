package com.example.pandora.Class;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String userName;
    private String password;
    private String name= "User" ;
    private String numberPhone;
    private int role= 0;
    boolean roleUser= false;
    boolean roleCategory= false;
    boolean roleRestaurant= false;
    boolean roleReview= false;
    private String image;

    public boolean isRoleReview() {
        return roleReview;
    }

    public User(int id, String userName, String password, String name, String numberPhone, int role, boolean roleUser, boolean roleCategory, boolean roleRestaurant, boolean roleReview, String image) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.numberPhone = numberPhone;
        this.role = role;
        this.roleUser = roleUser;
        this.roleCategory = roleCategory;
        this.roleRestaurant = roleRestaurant;
        this.roleReview = roleReview;
        this.image = image;
    }

    public void setRoleReview(boolean roleReview) {
        this.roleReview = roleReview;
    }



    public User(String userName, String name) {
        this.userName = userName;
        this.name = name;
    }

    public User(String userName, String password, String numberPhone, int role) {
        this.userName = userName;
        this.password = password;
        this.numberPhone = numberPhone;
        this.role = role;
    }

    public User(String userName, String password, String name, String numberPhone, int role, String image) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.numberPhone = numberPhone;
        this.role = role;
        this.image = image;
    }

    public User(int id, String userName, String password, String name, String numberPhone, int role, boolean roleUser, boolean roleCategory, boolean roleRestaurant, String image) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.numberPhone = numberPhone;
        this.role = role;
        this.roleUser = roleUser;
        this.roleCategory = roleCategory;
        this.roleRestaurant = roleRestaurant;
        this.image = image;
    }

    public User(int id, String userName, String password, String name, String numberPhone, int role, String image ) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.numberPhone = numberPhone;
        this.image = image;
        this.role = role;
    }

    public User(int id, String userName, String password, String numberPhone) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.numberPhone = numberPhone;
    }

    public User(String userName, String password, String numberPhone) {
        this.userName = userName;
        this.password = password;
        this.numberPhone = numberPhone;
    }

    public boolean isRoleUser() {
        return roleUser;
    }

    public void setRoleUser(boolean roleUser) {
        this.roleUser = roleUser;
    }

    public boolean isRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(boolean roleCategory) {
        this.roleCategory = roleCategory;
    }

    public boolean isRoleRestaurant() {
        return roleRestaurant;
    }

    public void setRoleRestaurant(boolean roleRestaurant) {
        this.roleRestaurant = roleRestaurant;
    }

    public int isRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }
}
