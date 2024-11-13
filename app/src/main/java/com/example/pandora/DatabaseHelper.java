package com.example.pandora;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu
    private static final String DATABASE_NAME = "restaurant_db";
    private static final int DATABASE_VERSION = 2; // Tăng phiên bản để áp dụng nâng cấp

    // Cấu trúc bảng
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_REVIEW = "review";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_START = "start"; // Thêm cột start

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_RESTAURANTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_REVIEW + " TEXT, "
            + COLUMN_IMAGE + " INTEGER, "
            + COLUMN_START + " INTEGER)"; // Thêm cột start vào câu lệnh tạo bảng

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE); // Tạo bảng khi cơ sở dữ liệu được tạo lần đầu tiên
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // Kiểm tra nếu phiên bản trước đó nhỏ hơn 2
            db.execSQL("ALTER TABLE " + TABLE_RESTAURANTS + " ADD COLUMN " + COLUMN_START + " INTEGER"); // Thêm cột start
        }
    }
}
