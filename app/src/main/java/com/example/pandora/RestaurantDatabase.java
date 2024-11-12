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
        // Logic to add the restaurant to the database
        ContentValues values = new ContentValues();
        values.put("name", restaurant.getName());
        values.put("review", restaurant.getReview());
        values.put("image", restaurant.getImageResId());

        // Inserting the restaurant into the database
        long id = database.insert("restaurants", null, values); // Automatically generates id
        restaurant.setId((int) id);  // Set the ID from the inserted record
    }

    // Lấy danh sách các quán ăn từ cơ sở dữ liệu
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                DatabaseHelper.COLUMN_ID,  // Get the ID column
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_REVIEW,
                DatabaseHelper.COLUMN_IMAGE
        };

        // Query the restaurants table
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Retrieve the columns
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));  // Get the ID
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String review = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REVIEW));
                @SuppressLint("Range") int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));

                // Add the restaurant to the list with the ID
                restaurantList.add(new Restaurant(id, name, review, imageResId));  // Use the constructor with id
            }
            cursor.close();
        }
        return restaurantList;
    }
}
