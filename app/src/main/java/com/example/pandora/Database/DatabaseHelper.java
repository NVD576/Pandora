package com.example.pandora.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu
    private static final String DATABASE_NAME = "reviewFood.db";
    private static final int DATABASE_VERSION = 1; // Cập nhật phiên bản lên 4 để áp dụng nâng cấp

    // Cấu trúc bảng restaurants
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address"; // Thêm cột address
    public static final String COLUMN_LOCATION_ID = "locationid"; // Thêm cột locationid
    public static final String COLUMN_CATE_ID = "cateid"; // Thêm cột cateid
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_STAR = "start"; // Thêm cột start
    public static final String COLUMN_HISTORY = "history";

    // Cấu trúc bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_TAIKHOAN = "taiKhoan";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_SDT = "SDT";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_ROLE = "role";
    public static final String COLUMN_USER_IMAGE = "image";

    // Câu lệnh tạo bảng restaurants
    private static final String CREATE_TABLE_RESTAURANTS = "CREATE TABLE " + TABLE_RESTAURANTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_ADDRESS + " TEXT, "  // Thêm cột address
            + COLUMN_LOCATION_ID + " INTEGER, "  // Thêm cột locationid
            + COLUMN_CATE_ID + " INTEGER, "  // Thêm cột cateid
            + COLUMN_HISTORY + " INTEGER, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_STAR + " INTEGER)"; // Thêm cột start vào câu lệnh tạo bảng

    // Câu lệnh tạo bảng users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_TAIKHOAN + " TEXT, "
            + COLUMN_USER_PASSWORD + " TEXT, "
            + COLUMN_USER_NAME + " TEXT, "
            + COLUMN_USER_SDT + " TEXT, "
            + COLUMN_USER_ROLE + " INTEGER, "
            + COLUMN_USER_IMAGE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RESTAURANTS); // Tạo bảng restaurants
        db.execSQL(CREATE_TABLE_USERS);       // Tạo bảng users
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
