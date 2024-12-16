package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.pandora.Class.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;


    String[] columns = {
            DatabaseHelper.COLUMN_IMAGE_ID,
            DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID,
            DatabaseHelper.COLUMN_IMAGE_URL
    };
    public ImageDatabase(Context context) {
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

    // Thêm một image vào cơ sở dữ liệu
    public void addImage(Image image) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID, image.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_IMAGE_URL, image.getRestaurantid());

        long id = database.insert(DatabaseHelper.TABLE_IMAGES, null, values);
        if (id != -1) {
            image.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm đánh giá vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các image
    public List<Image> getAllImage() {
        List<Image> imageList = new ArrayList<>();
        Cursor cursor = null;

        try {


            cursor = database.query(DatabaseHelper.TABLE_IMAGES, columns, null, null, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));

                imageList.add(new Image(id, restaurantId, imageUrl));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageList;
    }

    // Lấy danh sách các đánh giá theo restaurantId
    public List<Image> getImageByRestaurantId(int restaurantID) {
        List<Image> imageList = new ArrayList<>();
        Cursor cursor = null;

        try {

            // Thêm điều kiện WHERE để lọc theo restaurantId
            String selection = DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID + " = ?";
            String[] selectionArgs = {String.valueOf(restaurantID)};

            // Truy vấn cơ sở dữ liệu với điều kiện restaurantId
            cursor = database.query(DatabaseHelper.TABLE_IMAGES, columns, selection, selectionArgs, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));
                imageList.add(new Image(id, restaurantId, imageUrl));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageList;
    }

    // Cập nhật image
    public void updateImage(Image image) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMAGE_ID, image.getId());
        values.put(DatabaseHelper.COLUMN_IMAGE_RESTAURANT_ID, image.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_IMAGE_URL, image.getRestaurantid());


        int rowsUpdated = database.update(DatabaseHelper.TABLE_IMAGES, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(image.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật đánh giá có ID: " + image.getId());
        }
    }

    // Xóa image theo ID
    public void deleteImage(int imageid) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_IMAGES,
                DatabaseHelper.COLUMN_IMAGE_ID + " = ?", new String[]{String.valueOf(imageid)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa đánh giá có ID: " + imageid);
        }
    }

    // Xóa tất cả các đánh giá
    public void deleteAllImages() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_IMAGES, null, null);
        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa tất cả các đánh giá.");
        }
    }

}
