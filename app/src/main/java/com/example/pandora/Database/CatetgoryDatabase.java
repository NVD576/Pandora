package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.example.pandora.Class.Category;
import java.util.ArrayList;
import java.util.List;

public class CatetgoryDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public CatetgoryDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    String[] columns = {
            DatabaseHelper.COLUMN_CATEGORY_ID,
            DatabaseHelper.COLUMN_CATEGORY_NAME
    };
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

    // Kiểm tra xem bảng có trống không
    public boolean isTableEmpty() {
        Cursor cursor = null;
        try {
            // Thực hiện câu lệnh SELECT COUNT(*)
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CATEGORIES;
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0); // Lấy giá trị COUNT(*)
                return count == 0; // Trả về true nếu bảng trống
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi kiểm tra bảng: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Đảm bảo đóng cursor
            }
        }
        return true; // Nếu có lỗi xảy ra, mặc định trả về true
    }


    // Kiểm tra xem category có tồn tại hay không (không phân biệt hoa thường)
    public boolean isCategoryExists(String categoryName) {
        Cursor cursor = null;
        try {
            String selection = "LOWER(" + DatabaseHelper.COLUMN_CATEGORY_NAME + ") = ?";
            String[] selectionArgs = {categoryName.toLowerCase()}; // Chuẩn hóa tên người dùng nhập về chữ thường

            cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, columns, selection, selectionArgs, null, null, null);

            // Nếu có bản ghi nào được tìm thấy, địa điểm đã tồn tại
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            throw new SQLException("Lỗi khi kiểm tra địa điểm: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Thêm  vào cơ sở dữ liệu
    public void addCategory(Category category) {
        if (isCategoryExists(category.getName())) {
            // Nếu địa điểm đã tồn tại, bỏ qua và không thêm
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName().trim()); // Chuẩn hóa tên (bỏ khoảng trắng thừa)

        // Thêm địa điểm vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);

        if (id != -1) {
            category.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm địa điểm vào cơ sở dữ liệu.");
        }
    }

    public Category getCategoryById(int categoryID) {
        Cursor cursor = null;
        try {

            String selection = DatabaseHelper.COLUMN_CATEGORY_ID + " =?";
            String[] selectionArgs = {String.valueOf(categoryID)}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            if (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                return(new Category(id, name));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
    // Lấy danh sách tất cả
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_CATEGORY_ID,
                    DatabaseHelper.COLUMN_CATEGORY_NAME
            };

            cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, columns, null, null, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                categoryList.add(new Category(id, name));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return categoryList;
    }

    // Lấy  theo tên
    public List<Category> getCategoryByName(String name) {
        List<Category> categoryList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_CATEGORY_ID,
                    DatabaseHelper.COLUMN_CATEGORY_NAME
            };

            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_CATEGORY_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%"}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                categoryList.add(new Category(id, categoryName));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return categoryList;
    }

    // Cập nhật thông tin địa điểm
    public void updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());

        // Cập nhật địa điểm theo ID
        int rowsUpdated = database.update(DatabaseHelper.TABLE_CATEGORIES, values,
                DatabaseHelper.COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(category.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật địa điểm có ID: " + category.getId());
        }
    }

    // Xóa một địa điểm theo ID
    public void deleteCategory(int categoryid) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryid)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa địa điểm có ID: " + categoryid);
        }
    }

    // Xóa tất cả các địa điểm
    public void deleteAllCategories() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_CATEGORIES, null, null);
        if (rowsDeleted == 0) {


            throw new SQLException("Không thể xóa tất cả các địa điểm.");
        }
    }
}
