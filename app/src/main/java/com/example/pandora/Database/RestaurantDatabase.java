package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

import com.example.pandora.Class.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public RestaurantDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở kết nối đến cơ sở dữ liệu
    public void open() throws SQLException {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            throw new SQLException("Không thể mở cơ sở dữ liệu.", e);
        }
    }

    // Đóng kết nối
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // Thêm một quán ăn vào cơ sở dữ liệu
    public void addRestaurant(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_REVIEW, restaurant.getReview());
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImageResId());
        values.put(DatabaseHelper.COLUMN_START, restaurant.getStart()); // Thêm thuộc tính start

        // Thêm quán ăn vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert(DatabaseHelper.TABLE_RESTAURANTS, null, values);
        if (id != -1) {
            restaurant.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm quán ăn vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các quán ăn từ cơ sở dữ liệu
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_ID,
                    DatabaseHelper.COLUMN_NAME,
                    DatabaseHelper.COLUMN_REVIEW,
                    DatabaseHelper.COLUMN_IMAGE,
                    DatabaseHelper.COLUMN_START
            };

            // Truy vấn bảng restaurants
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);

            // Kiểm tra nếu cursor không null và di chuyển đến bản ghi đầu tiên
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String review = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                int start = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START));

                // Thêm quán ăn vào danh sách với thuộc tính start
                restaurantList.add(new Restaurant(id,name, review, imageResId, start));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return restaurantList;
    }

    // Cập nhật thông tin một quán ăn
    public void updateRestaurant(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_REVIEW, restaurant.getReview());
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImageResId());
        values.put(DatabaseHelper.COLUMN_START, restaurant.getStart());

        // Cập nhật quán ăn theo ID
        int rowsUpdated = database.update(DatabaseHelper.TABLE_RESTAURANTS, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(restaurant.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật quán ăn có ID: " + restaurant.getId());
        }
    }

    // Xóa quán ăn theo ID
    public void deleteRestaurant(int restaurantId) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_RESTAURANTS,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(restaurantId)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa quán ăn có ID: " + restaurantId);
        }
    }

    // Xóa tất cả các quán ăn
    public void deleteAllRestaurants() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_RESTAURANTS, null, null);
        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa tất cả các quán ăn.");
        }
    }
}
