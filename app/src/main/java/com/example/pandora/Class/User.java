package com.example.pandora.Class;

public class User {
    private int id;
    private String taiKhoan;
    private String password;
    private String name;
    private String SDT;
    private String Image;

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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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
