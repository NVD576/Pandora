package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabase {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public RestaurantDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở kết nối đến cơ sở dữ liệu
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    // Đóng kết nối
    public void close() {
        dbHelper.close();
    }

    // Thêm một quán ăn vào cơ sở dữ liệu
    public void addRestaurant(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put("name", restaurant.getName());
        values.put("review", restaurant.getReview());
        values.put("image", restaurant.getImageResId());
        values.put("start", restaurant.getStart()); // Thêm thuộc tính start

        // Thêm quán ăn vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert("restaurants", null, values);
        restaurant.setId((int) id);
    }

    // Lấy danh sách các quán ăn từ cơ sở dữ liệu
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_REVIEW,
                DatabaseHelper.COLUMN_IMAGE,
                DatabaseHelper.COLUMN_START // Thêm cột start
        };

        // Truy vấn bảng restaurants
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Lấy dữ liệu từ các cột
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String review = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REVIEW));
                @SuppressLint("Range") int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
                @SuppressLint("Range") int start = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_START));

                // Thêm quán ăn vào danh sách với thuộc tính start
                restaurantList.add(new Restaurant(name, review, imageResId, start));
            }
            cursor.close();
        }
        return restaurantList;
    }
}
