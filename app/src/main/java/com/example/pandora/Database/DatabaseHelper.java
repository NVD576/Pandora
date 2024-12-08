package com.example.pandora.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu
    private static final String DATABASE_NAME = "reviewFood.db";
    private static final int DATABASE_VERSION = 2; // Cập nhật phiên bản để kích hoạt onUpgrade

    // Cấu trúc bảng restaurants
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LOCATION_ID = "locationid";
    public static final String COLUMN_CATE_ID = "cateid";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_STAR = "star";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_HISTORY = "history";

    // Cấu trúc bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_TAIKHOAN = "taiKhoan";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_SDT = "SDT";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_ROLE = "role";
    public static final String COLUMN_USER_ROLE_USER = "roleUser";
    public static final String COLUMN_USER_ROLE_CATEGORY = "roleCategory";
    public static final String COLUMN_USER_ROLE_RESTAURANT = "roleRestaurant";
    public static final String COLUMN_USER_ROLE_REVIEW = "roleReview";
    public static final String COLUMN_USER_IMAGE = "image";

    // Cấu trúc bảng reviews
    public static final String TABLE_REVIEWS = "reviews";
    public static final String COLUMN_REVIEW_ID = "id";
    public static final String COLUMN_REVIEW_USER_ID = "user_id";
    public static final String COLUMN_REVIEW_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_REVIEW_REVIEW = "review";
    public static final String COLUMN_REVIEW_DATE = "date";

    // Cấu trúc bảng locations
    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_LOCATION_LOCATION_ID = "id";
    public static final String COLUMN_LOCATION_LOCATION_NAME = "name";

    // Cấu trúc bảng ratings
    public static final String TABLE_RATINGS = "ratings";
    public static final String COLUMN_RATING_ID = "id";
    public static final String COLUMN_RATING_USER_ID = "user_id";
    public static final String COLUMN_RATING_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_RATING_STAR = "rating";


    // Câu lệnh tạo bảng restaurants
    private static final String CREATE_TABLE_RESTAURANTS = "CREATE TABLE " + TABLE_RESTAURANTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_ADDRESS + " TEXT, "
            + COLUMN_LOCATION_ID + " INTEGER, "
            + COLUMN_CATE_ID + " INTEGER, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_STAR + " INTEGER, "
            + COLUMN_HISTORY + " INTEGER)";

    // Câu lệnh tạo bảng users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_TAIKHOAN + " TEXT, "
            + COLUMN_USER_PASSWORD + " TEXT, "
            + COLUMN_USER_NAME + " TEXT, "
            + COLUMN_USER_SDT + " TEXT, "
            + COLUMN_USER_ROLE + " INTEGER, "
            + COLUMN_USER_ROLE_USER + " INTEGER, "  // Add roleUser column
            + COLUMN_USER_ROLE_CATEGORY + " INTEGER, " // Add roleCategory column
            + COLUMN_USER_ROLE_RESTAURANT + " INTEGER, "
            + COLUMN_USER_ROLE_REVIEW + " INTEGER, "
            + COLUMN_USER_IMAGE + " TEXT)";

    // Câu lệnh tạo bảng reviews
    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS + " ("
            + COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_REVIEW_USER_ID + " INTEGER, "
            + COLUMN_REVIEW_RESTAURANT_ID + " INTEGER, "
            + COLUMN_REVIEW_REVIEW + " TEXT, "
            + COLUMN_REVIEW_DATE + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
            + "FOREIGN KEY(" + COLUMN_REVIEW_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_ID + "))";



    // Câu lệnh tạo bảng locations
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " ("
            + COLUMN_LOCATION_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LOCATION_LOCATION_NAME + " TEXT)";



    // Câu lệnh tạo bảng ratings
    private static final String CREATE_TABLE_RATINGS = "CREATE TABLE " + TABLE_RATINGS + " ("
            + COLUMN_RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RATING_USER_ID + " INTEGER, "
            + COLUMN_RATING_RESTAURANT_ID + " INTEGER, "
            + COLUMN_RATING_STAR + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_RATING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
            + "FOREIGN KEY(" + COLUMN_RATING_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_ID + "))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RESTAURANTS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_REVIEWS); // Tạo bảng reviews
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_RATINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_TABLE_REVIEWS); // Thêm bảng reviews khi nâng cấp cơ sở dữ liệu
        }
    }
}
