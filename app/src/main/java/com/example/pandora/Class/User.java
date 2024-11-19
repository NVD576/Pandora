package com.example.pandora.Class;

public class User {
    private int id;
    private String taiKhoan;
    private String password;
    private String SDT;

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
