package com.example.pandora.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.pandora.Class.User;
import com.example.pandora.Profile;

import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    String[] columns = {
            DatabaseHelper.COLUMN_USER_ID,
            DatabaseHelper.COLUMN_USER_TAIKHOAN,
            DatabaseHelper.COLUMN_USER_PASSWORD,
            DatabaseHelper.COLUMN_USER_SDT,
            DatabaseHelper.COLUMN_USER_NAME,
            DatabaseHelper.COLUMN_USER_ROLE,
            DatabaseHelper.COLUMN_USER_IMAGE
    };
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
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_SDT, user.getSDT());
        values.put(DatabaseHelper.COLUMN_USER_ROLE, user.isRole() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_USER_IMAGE, user.getImage() != null ? user.getImage() : "");


        // Thực hiện chèn và nhận lại ID tự động tăng
        long id = database.insert(DatabaseHelper.TABLE_USERS, null, values);
        user.setId((int) id); // Gán ID tự động tăng vào đối tượng user
        return true;
    }

    // Kiểm tra xem bảng users có rỗng hay không
    public boolean isUserTableEmpty() {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count == 0; // Nếu số lượng bản ghi là 0, bảng rỗng
        }
        return true; // Trường hợp lỗi hoặc không thể truy vấn
    }

    @SuppressLint("Range")
    public User getUserByUsernameAndPassword(String userName, String password) {
        String selection = DatabaseHelper.COLUMN_USER_TAIKHOAN + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {userName, password};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id= cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
            String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
            String pass = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
            String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
            boolean role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE)) == 1;
            String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

            User user = new User(id,taiKhoan, pass,name, SDT, role, image);
            cursor.close();
            return user;
        }
        return null;
    }

    public void updateUserImage(int userId, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_IMAGE, imagePath);

        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        database.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
    }

    public void updateUser(User user) {
        ContentValues values = new ContentValues();

        // Cập nhật các trường cần thiết
        values.put(DatabaseHelper.COLUMN_USER_TAIKHOAN, user.getTaiKhoan());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_SDT, user.getSDT());
        values.put(DatabaseHelper.COLUMN_USER_ROLE, user.isRole() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_USER_IMAGE, user.getImage() != null ? user.getImage() : "");

        // Điều kiện để xác định người dùng cần cập nhật (theo ID)
        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(user.getId())};

        // Cập nhật dữ liệu trong cơ sở dữ liệu
        database.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
    }


    @SuppressLint("Range")
    public String getUserImage(int userId) {
        String imagePath = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,       // Tên bảng
                new String[]{DatabaseHelper.COLUMN_USER_IMAGE}, // Cột cần lấy
                DatabaseHelper.COLUMN_USER_ID + " = ?", // Điều kiện
                new String[]{String.valueOf(userId)},    // Giá trị điều kiện
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));
            cursor.close();
        }
        return imagePath;
    }


    // Lấy danh sách tất cả người dùng
    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
                String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
                String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
                boolean role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE)) == 1;
                String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

                User user = new User(id, taiKhoan, password, name, SDT , role, image);
                userList.add(user);
            }
            cursor.close();
        }
        return userList;
    }


    // Lấy thông tin người dùng theo ID
    @SuppressLint("Range")
    public User getUserById(int userId) {

        String selection = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
            String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
            String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
            boolean role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE)) == 1;
            String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

            User user = new User(taiKhoan, password, name, SDT,  role, image);
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
