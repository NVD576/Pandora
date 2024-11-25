package com.example.pandora.Class;

public class User {
    private int id;
    private String taiKhoan;
    private String password;
    private String name;
    private String SDT;
    private boolean role= false;
    private String image;

    public User(String taiKhoan, String password, String SDT, boolean role) {
        this.taiKhoan = taiKhoan;
        this.password = password;
        this.SDT = SDT;
        this.role = role;
    }

    public User(String taiKhoan, String password, String name, String SDT, boolean role, String image) {
        this.taiKhoan = taiKhoan;
        this.password = password;
        this.name = name;
        this.SDT = SDT;
        this.role = role;
        this.image = image;
    }

    public User(int id, String taiKhoan, String password, String name, String SDT, boolean role,String image ) {
        this.id = id;
        this.taiKhoan = taiKhoan;
        this.password = password;
        this.name = name;
        this.SDT = SDT;
        this.image = image;
        this.role = role;
    }

    public User(int id, String taiKhoan, String password, String SDT) {
        this.id = id;
        this.taiKhoan = taiKhoan;
        this.password = password;
        this.SDT = SDT;
    }

    public User(String taiKhoan, String password, String SDT) {
        this.taiKhoan = taiKhoan;
        this.password = password;
        this.SDT = SDT;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
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

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }
}
