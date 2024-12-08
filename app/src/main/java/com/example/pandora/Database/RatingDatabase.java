package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.pandora.Class.Rating;

import java.util.ArrayList;
import java.util.List;

public class RatingDatabase {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // Constructor
    public RatingDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở cơ sở dữ liệu
    public void open() throws SQLException {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            throw new SQLException("Không thể mở cơ sở dữ liệu.", e);
        }
    }

    // Đóng cơ sở dữ liệu
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // **Thêm một đánh giá mới**
    public void addRating(Rating rating) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RATING_USER_ID, rating.getUserid());
        values.put(DatabaseHelper.COLUMN_RATING_RESTAURANT_ID, rating.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_RATING_STAR, rating.getStar());

        long id = database.insert(DatabaseHelper.TABLE_RATINGS, null, values);
        if (id != -1) {
            rating.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm địa điểm vào cơ sở dữ liệu.");
        }
    }

    // **Cập nhật đánh giá**
    public int updateRating(int userid, int restaurantid, int newStar) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RATING_STAR, newStar);

        // Điều kiện lọc: userid và restaurantid
        String whereClause = DatabaseHelper.COLUMN_RATING_USER_ID + " =? AND "
                + DatabaseHelper.COLUMN_RATING_RESTAURANT_ID + " =?";
        String[] whereArgs = {String.valueOf(userid), String.valueOf(restaurantid)};

        return database.update(DatabaseHelper.TABLE_RATINGS, values, whereClause, whereArgs);
    }

    public Rating getRatingById(int userid, int restaurantid) {
        Cursor cursor = null;
        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_RATING_ID,
                    DatabaseHelper.COLUMN_RATING_USER_ID,
                    DatabaseHelper.COLUMN_RATING_RESTAURANT_ID,
                    DatabaseHelper.COLUMN_RATING_STAR
            };

            // Điều kiện truy vấn: tìm kiếm theo userid và restaurantid
            String selection = DatabaseHelper.COLUMN_RATING_USER_ID + " =? AND "
                    + DatabaseHelper.COLUMN_RATING_RESTAURANT_ID + " =?";
            String[] selectionArgs = {String.valueOf(userid), String.valueOf(restaurantid)};

            cursor = database.query(DatabaseHelper.TABLE_RATINGS, columns, selection, selectionArgs, null, null, null);

            // Kiểm tra xem cursor có dữ liệu không và lấy thông tin đánh giá
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_USER_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_RESTAURANT_ID));
                int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_STAR));

                // Trả về đối tượng Rating
                return new Rating(id, userId, restaurantId, star);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null; // Nếu không tìm thấy đánh giá
    }

    // **Xóa một đánh giá**
    public int deleteRating(int id) {
        String whereClause = DatabaseHelper.COLUMN_RATING_ID + " =?";
        String[] whereArgs = {String.valueOf(id)};

        return database.delete(DatabaseHelper.TABLE_RATINGS, whereClause, whereArgs);
    }

    // **Lấy danh sách đánh giá của một nhà hàng**
    public List<Rating> getRatingsByRestaurantId(int restaurantId) {
        List<Rating> ratings = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = DatabaseHelper.COLUMN_RATING_RESTAURANT_ID + " =?";
            String[] selectionArgs = {String.valueOf(restaurantId)};
            cursor = database.query(DatabaseHelper.TABLE_RATINGS, null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_USER_ID));
                    int stars = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_STAR));
                    ratings.add(new Rating(id, userId, restaurantId, stars));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ratings;
    }

    // **Tính số sao trung bình của một nhà hàng**
    public int getAverageRating(int restaurantId) {
        Cursor cursor = null;
        try {
            String query = "SELECT AVG(" + DatabaseHelper.COLUMN_RATING_STAR + ") AS avg_rating FROM "
                    + DatabaseHelper.TABLE_RATINGS + " WHERE "
                    + DatabaseHelper.COLUMN_RATING_RESTAURANT_ID + " =?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(restaurantId)});

            if (cursor != null && cursor.moveToFirst()) {
                // Kiểm tra nếu có giá trị avg_rating trả về
                int avgRating = cursor.getInt(cursor.getColumnIndexOrThrow("avg_rating"));
                if (avgRating != 0) {
                    return avgRating;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0; // Nếu không có đánh giá hoặc không tìm thấy restaurant_id, trả về 0
    }

    // **Lấy tất cả đánh giá**
    public List<Rating> getAllRatings() {
        List<Rating> ratings = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.TABLE_RATINGS, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_USER_ID));
                    int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_RESTAURANT_ID));
                    int stars = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING_STAR));
                    ratings.add(new Rating(id, userId, restaurantId, stars));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ratings;
    }
}
