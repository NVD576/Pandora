package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.pandora.Class.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public LocationDatabase(Context context) {
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

    // Kiểm tra xem bảng có trống không
    public boolean isTableEmpty() {
        Cursor cursor = null;
        try {
            // Thực hiện câu lệnh SELECT COUNT(*)
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_LOCATIONS;
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


    // Kiểm tra xem địa điểm có tồn tại hay không (không phân biệt hoa thường)
    public boolean isLocationExists(String locationName) {
        Cursor cursor = null;
        try {
            String[] columns = {DatabaseHelper.COLUMN_LOCATION_LOCATION_ID};
            String selection = "LOWER(" + DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME + ") = ?";
            String[] selectionArgs = {locationName.toLowerCase()}; // Chuẩn hóa tên người dùng nhập về chữ thường

            cursor = database.query(DatabaseHelper.TABLE_LOCATIONS, columns, selection, selectionArgs, null, null, null);

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

    // Thêm một địa điểm vào cơ sở dữ liệu
    public void addLocation(Location location) {
        if (isLocationExists(location.getName())) {
            // Nếu địa điểm đã tồn tại, bỏ qua và không thêm
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME, location.getName().trim()); // Chuẩn hóa tên (bỏ khoảng trắng thừa)

        // Thêm địa điểm vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert(DatabaseHelper.TABLE_LOCATIONS, null, values);
        if (id != -1) {
            location.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm địa điểm vào cơ sở dữ liệu.");
        }
    }

    public Location getLocationById(int locationID) {
        Cursor cursor = null;
        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_ID,
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME
            };
            String selection = DatabaseHelper.COLUMN_LOCATION_LOCATION_ID + " =?";
            String[] selectionArgs = {String.valueOf(locationID)}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_LOCATIONS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            if (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME));
                return(new Location(id, name));
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
    // Lấy danh sách tất cả các địa điểm
    public List<Location> getAllLocations() {
        List<Location> locationList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_ID,
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME
            };

            cursor = database.query(DatabaseHelper.TABLE_LOCATIONS, columns, null, null, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME));
                locationList.add(new Location(id, name));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return locationList;
    }

    // Lấy một địa điểm theo tên
    public List<Location> getLocationsByName(String name) {
        List<Location> locationList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_ID,
                    DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME
            };

            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%"}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_LOCATIONS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_ID));
                String locationName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME));
                locationList.add(new Location(id, locationName));
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return locationList;
    }

    // Cập nhật thông tin địa điểm
    public void updateLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME, location.getName());

        // Cập nhật địa điểm theo ID
        int rowsUpdated = database.update(DatabaseHelper.TABLE_LOCATIONS, values,
                DatabaseHelper.COLUMN_LOCATION_LOCATION_ID + " = ?", new String[]{String.valueOf(location.getId())});

        if (rowsUpdated == 0) {
            throw new SQLException("Không thể cập nhật địa điểm có ID: " + location.getId());
        }
    }

    // Xóa một địa điểm theo ID
    public void deleteLocation(int locationId) {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_LOCATIONS,
                DatabaseHelper.COLUMN_LOCATION_LOCATION_ID + " = ?", new String[]{String.valueOf(locationId)});

        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa địa điểm có ID: " + locationId);
        }
    }

    // Xóa tất cả các địa điểm
    public void deleteAllLocations() {
        int rowsDeleted = database.delete(DatabaseHelper.TABLE_LOCATIONS, null, null);
        if (rowsDeleted == 0) {
            throw new SQLException("Không thể xóa tất cả các địa điểm.");
        }
    }
}
