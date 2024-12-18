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
    Cursor  cursor=null;
    public RestaurantDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_ADDRESS,
            DatabaseHelper.COLUMN_LOCATION_ID,
            DatabaseHelper.COLUMN_CATE_ID,
            DatabaseHelper.COLUMN_STAR,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_HISTORY
    };
    ContentValues values(Restaurant restaurant){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, restaurant.getAddress());  // Thêm địa chỉ
        values.put(DatabaseHelper.COLUMN_LOCATION_ID, restaurant.getLocationid());  // Thêm locationid
        values.put(DatabaseHelper.COLUMN_CATE_ID, restaurant.getCateid());  // Thêm cateid
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, restaurant.getDescription());
        values.put(DatabaseHelper.COLUMN_STAR, restaurant.getStar());
        return values;
    }

    Restaurant restaurant(){
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
        int locationid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
        int cateid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
        int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
        return new Restaurant(id, name,address,locationid,cateid, star,description);
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

        // Thêm quán ăn vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert(DatabaseHelper.TABLE_RESTAURANTS, null, values(restaurant));
        // Dữ liệu được lưu dưới dạng một Map hoặc đối tượng
        if (id != -1) {
            restaurant.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm quán ăn vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các quán ăn từ cơ sở dữ liệu
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);
            // Kiểm tra nếu cursor không null và di chuyển đến bản ghi đầu tiên
            while (cursor != null && cursor.moveToNext()) {
                // Thêm quán ăn vào danh sách
                restaurantList.add(restaurant()); // Chưa dùng address, locationid, cateid trong constructor

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

    // Lấy danh sách các quán ăn có số sao trên 4
    public List<Restaurant> getRestaurantsAbove4Stars() {
        List<Restaurant> restaurantList = new ArrayList<>();

        try {
            // Sử dụng câu truy vấn WHERE để lọc các nhà hàng có số sao > 4
            String selection = DatabaseHelper.COLUMN_STAR + " >= ?";
            String[] selectionArgs = {"4"}; // Điều kiện sao lớn hơn 4

            // Thực hiện truy vấn cơ sở dữ liệu
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua các kết quả và thêm vào danh sách restaurantList
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    restaurantList.add(restaurant()); // Tạo đối tượng Restaurant từ cursor
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            throw new SQLException("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng cursor
            }
        }

        return restaurantList; // Trả về danh sách các nhà hàng
    }


    // Lấy danh sách các quán ăn theo số sao giảm dần
    public List<Restaurant> getHighRatedRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            // Truy vấn cơ sở dữ liệu và sắp xếp theo số sao giảm dần
            String orderBy = DatabaseHelper.COLUMN_STAR + " DESC"; // Sắp xếp theo số sao giảm dần

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, orderBy);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                restaurantList.add(restaurant());
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



    // Lấy danh sách theo id
    public Restaurant getRestaurantsByID(int restaurantId) {
        try {
            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_ID + " =?";
            String[] selectionArgs = {String.valueOf(restaurantId)}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);
            // Duyệt qua kết quả và thêm vào danh sách
            if (cursor != null && cursor.moveToNext()) {
                return restaurant();
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

    //Lấy danh sách các quán ăn theo tên
    public List<Restaurant> getRestaurantsByName(String name) {
        List<Restaurant> restaurantList = new ArrayList<>();

        try {
            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%"}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {

                restaurantList.add(restaurant());
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

    //Lấy danh sách theo location
    public List<Restaurant> getRestaurantsByLocation(int locationid) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            // Sử dụng câu truy vấn WHERE để lọc theo locationid
            String selection = DatabaseHelper.COLUMN_LOCATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(locationid)}; // Tìm kiếm nhà hàng theo location_id

            // Thực hiện truy vấn cơ sở dữ liệu
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua các kết quả và thêm vào danh sách restaurantList
            while (cursor != null && cursor.moveToNext()) {
                restaurantList.add(restaurant());
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

        // Cập nhật quán ăn theo ID
        int rowsUpdated = database.update(DatabaseHelper.TABLE_RESTAURANTS, values(restaurant),
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
