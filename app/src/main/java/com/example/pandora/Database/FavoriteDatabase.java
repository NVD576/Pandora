package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pandora.Class.Favorite;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    // Các cột cần truy vấn
    String[] columns = {
            DatabaseHelper.COLUMN_FAVORITE_ID,
            DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID,
            DatabaseHelper.COLUMN_FAVORITE_USER_ID,
            DatabaseHelper.COLUMN_FAVORITE_LIKE
    };

    public FavoriteDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở kết nối đến cơ sở dữ liệu
    public void open() throws SQLException {
        try {
            if (database == null || !database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
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

    // Thêm một favorite vào cơ sở dữ liệu
    public void addFavorite(Favorite favorite) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID, favorite.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_FAVORITE_USER_ID, favorite.getUserid());
        values.put(DatabaseHelper.COLUMN_FAVORITE_LIKE, favorite.getLike());

        long id = database.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
        if (id != -1) {
            favorite.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm yêu thích vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các favorite
    public List<Favorite> getAllFavorites() {
        List<Favorite> favoriteList = new ArrayList<>();
        try (Cursor cursor = database.query(DatabaseHelper.TABLE_FAVORITES, columns, null, null, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_USER_ID));
                int like = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_LIKE));

                favoriteList.add(new Favorite(id, restaurantId, userId, like));
            }
        } catch (Exception e) {
            Log.e("FavoriteDatabase", "Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        }
        return favoriteList;
    }

    public List<Favorite> getFavoritesByUserId(int userId) {
        List<Favorite> favoriteList = new ArrayList<>();
        try (Cursor cursor = database.query(DatabaseHelper.TABLE_FAVORITES, columns,
                DatabaseHelper.COLUMN_FAVORITE_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, null)) {

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID));
                int like = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_LIKE));

                favoriteList.add(new Favorite(id, restaurantId, userId, like));
            }
        } catch (Exception e) {
            Log.e("FavoriteDatabase", "Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        }
        return favoriteList;
    }


    // Lấy danh sách các favorite theo userId
    public Favorite getFavoriteById(int userId, int restaurantId) {
        Favorite favorite = null;
        Cursor cursor = null;

        try {
            // Câu truy vấn lọc theo cả userId và restaurantId
            cursor = database.query(DatabaseHelper.TABLE_FAVORITES, columns,
                    DatabaseHelper.COLUMN_FAVORITE_USER_ID + " = ? AND " +
                            DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(restaurantId)},
                    null, null, null);

            // Nếu có ít nhất một kết quả, lấy dữ liệu của dòng đầu tiên
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_ID));
                int like = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_LIKE));

                // Tạo đối tượng Favorite và gán giá trị
                favorite = new Favorite(id, restaurantId, userId, like);
            }
        } catch (Exception e) {
            Log.e("FavoriteDatabase", "Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return favorite;
    }


    // Cập nhật favorite
    public void updateFavorite(Favorite favorite) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FAVORITE_RESTAURANT_ID, favorite.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_FAVORITE_USER_ID, favorite.getUserid());
        values.put(DatabaseHelper.COLUMN_FAVORITE_LIKE, favorite.getLike());

        int rowsUpdated = database.update(DatabaseHelper.TABLE_FAVORITES, values,
                DatabaseHelper.COLUMN_FAVORITE_ID + " = ?", new String[]{String.valueOf(favorite.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật yêu thích có ID: " + favorite.getId());
        }
    }

    // Xóa favorite theo ID
    public void deleteFavorite(int favoriteId) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_FAVORITES,
                DatabaseHelper.COLUMN_FAVORITE_ID + " = ?", new String[]{String.valueOf(favoriteId)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa yêu thích có ID: " + favoriteId);
        }
    }

    // Xóa tất cả các favorite
    public void deleteAllFavorites() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_FAVORITES, null, null);
        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa tất cả các yêu thích.");
        }
    }
}
