package com.example.pandora.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.pandora.Class.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            DatabaseHelper.COLUMN_USER_ROLE_USER,
            DatabaseHelper.COLUMN_USER_ROLE_CATEGORY,
            DatabaseHelper.COLUMN_USER_ROLE_RESTAURANT,
            DatabaseHelper.COLUMN_USER_ROLE_REVIEW,
            DatabaseHelper.COLUMN_USER_IMAGE
    };
    public UserDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    ContentValues values(User user){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_TAIKHOAN, user.getTaiKhoan());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_SDT, user.getSDT());
        values.put(DatabaseHelper.COLUMN_USER_ROLE, user.isRole());
        values.put(DatabaseHelper.COLUMN_USER_IMAGE, user.getImage() != null ? user.getImage() : "");

        // Insert additional role fields
        values.put(DatabaseHelper.COLUMN_USER_ROLE_USER, user.isRoleUser() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_USER_ROLE_CATEGORY, user.isRoleCategory() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_USER_ROLE_RESTAURANT, user.isRoleRestaurant() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_USER_ROLE_REVIEW, user.isRoleReview() ? 1 : 0);
        return values;
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
    public boolean isUserTaiKhoanExists(String userName) {
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
        if (isUserTaiKhoanExists(user.getTaiKhoan())) {
            // Tài khoản đã tồn tại, thông báo cho người dùng
            Toast.makeText(context, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
            return false;
        }


        // Thực hiện chèn và nhận lại ID tự động tăng
        long id = database.insert(DatabaseHelper.TABLE_USERS, null, values(user));
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
    User Search(String selection,String[] selectionArgs){
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id= cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
            String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
            String pass = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
            String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
            int role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE));
            String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

            boolean roleUser = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_USER)) == 1;
            boolean roleCategory = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_CATEGORY)) == 1;
            boolean roleRestaurant = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_RESTAURANT)) == 1;
            boolean roleReview = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_REVIEW)) == 1;


            User user = new User(id, taiKhoan, pass, name, SDT, role, roleUser, roleCategory, roleRestaurant,roleReview, image);
            cursor.close();
            return user;
        }
        return null;
    }

    //tìm user bằng tài khoản và mật khẩu
    @SuppressLint("Range")
    public User getUserByUsernameAndPassword(String userName, String password) {
        String selection = DatabaseHelper.COLUMN_USER_TAIKHOAN + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {userName, password};

        return Search(selection,selectionArgs);
    }

    //tìm user bằng số điện thoại
    @SuppressLint("Range")
    public User getUserBySDT(String sdt) {
        String selection = DatabaseHelper.COLUMN_USER_SDT + " = ? ";
        String[] selectionArgs = {sdt};

        return Search(selection,selectionArgs);
    }

    @SuppressLint("Range")
    public User getUserByTaiKhoan(String taikhoan) {
        String selection = DatabaseHelper.COLUMN_USER_TAIKHOAN + " = ? ";
        String[] selectionArgs = {taikhoan};

        return  Search(selection,selectionArgs);
    }

    // Lấy thông tin người dùng theo ID
    @SuppressLint("Range")
    public User getUserById(int userId) {

        String selection = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        return  Search(selection,selectionArgs);
    }

    public void updateUserImage(int userId, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_IMAGE, imagePath);

        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        database.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
    }

    public void updateUser(User user) {

        // Điều kiện để xác định người dùng cần cập nhật (theo ID)
        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(user.getId())};

        // Cập nhật dữ liệu trong cơ sở dữ liệu
        database.update(DatabaseHelper.TABLE_USERS, values(user), whereClause, whereArgs);
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
                int role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE));
                String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

                boolean roleUser = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_USER)) == 1;
                boolean roleCategory = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_CATEGORY)) == 1;
                boolean roleRestaurant = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_RESTAURANT)) == 1;
                boolean roleReview = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_REVIEW)) == 1;

                User user = new User(id, taiKhoan, password, name, SDT, role, roleUser, roleCategory, roleRestaurant,roleReview, image);
                userList.add(user);
            }
            cursor.close();
        }
        return userList;
    }

    @SuppressLint("Range")
    public List<User> getUsersByName(String name) {
        List<User> userList = new ArrayList<>();
        String selection = DatabaseHelper.COLUMN_USER_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + name + "%"};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
                String taiKhoan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TAIKHOAN));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD));
                String SDT = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_SDT));
                String userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
                int role = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE));
                String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));

                boolean roleUser = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_USER)) == 1;
                boolean roleCategory = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_CATEGORY)) == 1;
                boolean roleRestaurant = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_RESTAURANT)) == 1;
                boolean roleReview = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_REVIEW)) == 1;

                User user = new User(id, taiKhoan, password, userName, SDT, role, roleUser, roleCategory, roleRestaurant, roleReview, image);
                userList.add(user);
            }
            cursor.close();
        }
        return userList;
    }




    // Xóa một người dùng theo ID
    public void deleteUser(int userId) {
        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};
        database.delete(DatabaseHelper.TABLE_USERS, whereClause, whereArgs);
    }
}
