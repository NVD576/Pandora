package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserDatabase(Context context) {
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

    // Kiểm tra tài khoản đã tồn tại chưa
    public boolean isUsernameExists(String userName) {
        String[] columns = {DatabaseHelper.COLUMN_USER_TAIKHOAN};
        String selection = DatabaseHelper.COLUMN_USER_TAIKHOAN + " = ?";
        String[] selectionArgs = {userName};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true; // Tài khoản đã tồn tại
        }
        cursor.close();
        return false; // Tài khoản không tồn tại
    }

    // Thêm một người dùng vào cơ sở dữ liệu
    public boolean addUser(User user, Context context) {
        // Kiểm tra xem tài khoản đã tồn tại chưa
        if (isUsernameExists(user.getTaiKhoan())) {
            // Tài khoản đã tồn tại, thông báo cho người dùng
            Toast.makeText(context, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_TAIKHOAN, user.getTaiKhoan());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_USER_SDT, user.getSDT());

        // Thực hiện chèn và nhận lại ID tự động tăng
        long id = database.insert(DatabaseHelper.TABLE_USERS, null, values);
        user.setId((int) id); // Gán ID tự động tăng vào đối tượng user
        return true;
    }
    @SuppressLint("Range")
    public User getUserByUsernameAndPassword(String userName, String password) {
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USER_TAIKHOAN,
                DatabaseHelper.COLUMN_USER_PASSWORD,
                DatabaseHelper.COLUMN_USER_SDT
        };
        String selection = DatabaseHelper.COLUMN_USER_TAIKHOAN + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {userName, password};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
            String pass = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
            String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));

            User user = new User(taiKhoan, pass, SDT);
            user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)));
            cursor.close();
            return user;
        }

        // Nếu không tìm thấy tài khoản, trả về null
        return null;
    }




    // Lấy danh sách tất cả người dùng
    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USER_TAIKHOAN,
                DatabaseHelper.COLUMN_USER_PASSWORD,
                DatabaseHelper.COLUMN_USER_SDT
        };

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
                String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
                String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));

                User user = new User(id,taiKhoan, password, SDT);
                userList.add(user);
            }
            cursor.close();
        }
        return userList;
    }

    // Lấy thông tin người dùng theo ID
    @SuppressLint("Range")
    public User getUserById(int userId) {
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USER_TAIKHOAN,
                DatabaseHelper.COLUMN_USER_PASSWORD,
                DatabaseHelper.COLUMN_USER_SDT
        };
        String selection = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
            String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
            String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));

            User user = new User(taiKhoan, password, SDT);
            user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)));
            cursor.close();
            return user;
        }
        return null;
    }

    // Xóa một người dùng theo ID
    public void deleteUser(int userId) {
        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};
        database.delete(DatabaseHelper.TABLE_USERS, whereClause, whereArgs);
    }
}
