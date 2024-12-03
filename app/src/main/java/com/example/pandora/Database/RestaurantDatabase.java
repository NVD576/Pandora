package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
        values.put(DatabaseHelper.COLUMN_ADDRESS, restaurant.getAddress());  // Thêm địa chỉ
        values.put(DatabaseHelper.COLUMN_LOCATION_ID, restaurant.getLocationid());  // Thêm locationid
        values.put(DatabaseHelper.COLUMN_CATE_ID, restaurant.getCateid());  // Thêm cateid
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImage());
        values.put(DatabaseHelper.COLUMN_STAR, restaurant.getStar());
        values.put(DatabaseHelper.COLUMN_HISTORY, restaurant.getHistory());

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
                    DatabaseHelper.COLUMN_ADDRESS,
                    DatabaseHelper.COLUMN_LOCATION_ID,
                    DatabaseHelper.COLUMN_CATE_ID,
                    DatabaseHelper.COLUMN_IMAGE,
                    DatabaseHelper.COLUMN_STAR,
                    DatabaseHelper.COLUMN_HISTORY

            };

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);

            // Kiểm tra nếu cursor không null và di chuyển đến bản ghi đầu tiên
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                int locationid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                int cateid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                // Thêm quán ăn vào danh sách
                restaurantList.add(new Restaurant(id, name,address,locationid,cateid, image, star, history)); // Chưa dùng address, locationid, cateid trong constructor

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
        values.put(DatabaseHelper.COLUMN_ADDRESS, restaurant.getAddress());  // Cập nhật địa chỉ
        values.put(DatabaseHelper.COLUMN_LOCATION_ID, restaurant.getLocationid());  // Cập nhật locationid
        values.put(DatabaseHelper.COLUMN_CATE_ID, restaurant.getCateid());  // Cập nhật cateid
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImage());
        values.put(DatabaseHelper.COLUMN_STAR, restaurant.getStar());  // Cập nhật đánh giá
        values.put(DatabaseHelper.COLUMN_HISTORY, restaurant.getStar());

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
