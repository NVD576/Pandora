package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.pandora.Class.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public ReviewDatabase(Context context) {
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

    // Thêm một đánh giá vào cơ sở dữ liệu
    public void addReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REVIEW_USER_ID, review.getUserid());
        values.put(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID, review.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_REVIEW_REVIEW, review.getReview());
        values.put(DatabaseHelper.COLUMN_REVIEW_DATE, review.getDate());

        long id = database.insert(DatabaseHelper.TABLE_REVIEWS, null, values);
        if (id != -1) {
            review.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm đánh giá vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các đánh giá
    public List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_REVIEW_ID,
                    DatabaseHelper.COLUMN_REVIEW_USER_ID,
                    DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID,
                    DatabaseHelper.COLUMN_REVIEW_REVIEW,
                    DatabaseHelper.COLUMN_REVIEW_DATE
            };

            cursor = database.query(DatabaseHelper.TABLE_REVIEWS, columns, null, null, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_USER_ID));
                int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID));
                String reviewText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_REVIEW));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_DATE));

                reviewList.add(new Review(id, userId, restaurantId, reviewText, date));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reviewList;
    }
    // Lấy danh sách các đánh giá theo restaurantId
    public List<Review> getReviewsByRestaurantId(int restaurantId) {
        List<Review> reviewList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_REVIEW_ID,
                    DatabaseHelper.COLUMN_REVIEW_USER_ID,
                    DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID,
                    DatabaseHelper.COLUMN_REVIEW_REVIEW,
                    DatabaseHelper.COLUMN_REVIEW_DATE
            };

            // Thêm điều kiện WHERE để lọc theo restaurantId
            String selection = DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID + " = ?";
            String[] selectionArgs = {String.valueOf(restaurantId)};

            // Truy vấn cơ sở dữ liệu với điều kiện restaurantId
            cursor = database.query(DatabaseHelper.TABLE_REVIEWS, columns, selection, selectionArgs, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_USER_ID));
                int restaurantIdFromDb = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID));
                String reviewText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_REVIEW));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_DATE));

                reviewList.add(new Review(id, userId, restaurantIdFromDb, reviewText, date));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reviewList;
    }

    // Cập nhật đánh giá
    public void updateReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REVIEW_ID, review.getId());
        values.put(DatabaseHelper.COLUMN_REVIEW_USER_ID, review.getUserid());
        values.put(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID, review.getRestaurantid());
        values.put(DatabaseHelper.COLUMN_REVIEW_REVIEW, review.getReview());
        values.put(DatabaseHelper.COLUMN_REVIEW_DATE, review.getDate());

        int rowsUpdated = database.update(DatabaseHelper.TABLE_REVIEWS, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(review.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật đánh giá có ID: " + review.getId());
        }
    }

    // Xóa đánh giá theo ID
    public void deleteReview(int reviewId) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_REVIEWS,
                DatabaseHelper.COLUMN_REVIEW_ID + " = ?", new String[]{String.valueOf(reviewId)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa đánh giá có ID: " + reviewId);
        }
    }

    // Xóa tất cả các đánh giá
    public void deleteAllReviews() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_REVIEWS, null, null);
        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa tất cả các đánh giá.");
        }
    }

    // Kiểm tra xem một người dùng đã đánh giá nhà hàng hay chưa
    public boolean hasUserRated(int userId, int restaurantId) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_REVIEWS +
                    " WHERE " + DatabaseHelper.COLUMN_REVIEW_USER_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(restaurantId)});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
