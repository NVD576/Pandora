package com.example.pandora.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabase {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public RestaurantDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_ADDRESS,
            DatabaseHelper.COLUMN_LOCATION_ID,
            DatabaseHelper.COLUMN_CATE_ID,
            DatabaseHelper.COLUMN_IMAGE,
            DatabaseHelper.COLUMN_STAR,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_HISTORY
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

    // Thêm một quán ăn vào cơ sở dữ liệu
    public void addRestaurant(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, restaurant.getAddress());  // Thêm địa chỉ
        values.put(DatabaseHelper.COLUMN_LOCATION_ID, restaurant.getLocationid());  // Thêm locationid
        values.put(DatabaseHelper.COLUMN_CATE_ID, restaurant.getCateid());  // Thêm cateid
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImage());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, restaurant.getDescription());
        values.put(DatabaseHelper.COLUMN_STAR, restaurant.getStar());
        values.put(DatabaseHelper.COLUMN_HISTORY, restaurant.getHistory());

        // Thêm quán ăn vào cơ sở dữ liệu và gán ID từ bản ghi đã chèn
        long id = database.insert(DatabaseHelper.TABLE_RESTAURANTS, null, values);
        if (id != -1) {
            restaurant.setId((int) id);
        } else {
            throw new SQLException("Không thể thêm quán ăn vào cơ sở dữ liệu.");
        }
    }

    // Lấy danh sách các quán ăn từ cơ sở dữ liệu
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, null, null, null, null, null);

            // Kiểm tra nếu cursor không null và di chuyển đến bản ghi đầu tiên
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                int locationid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                int cateid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                // Thêm quán ăn vào danh sách
                restaurantList.add(new Restaurant(id, name,address,locationid,cateid, image, star,description, history)); // Chưa dùng address, locationid, cateid trong constructor

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
        Cursor cursor = null;
        try {
            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_ID + " =?";
            String[] selectionArgs = {String.valueOf(restaurantId)}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            if (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                int locationId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                int cateId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                return new Restaurant(id, restaurantName, address, locationId, cateId, image, star,description, history);
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
        Cursor cursor = null;

        try {


            // Sử dụng từ khóa LIKE để tìm kiếm tên gần đúng
            String selection = DatabaseHelper.COLUMN_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%"}; // Tìm kiếm các chuỗi có chứa `name`

            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua kết quả và thêm vào danh sách
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                int locationId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                int cateId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                restaurantList.add(new Restaurant(id, restaurantName, address, locationId, cateId, image, star,description, history));
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
        Cursor cursor = null;
        try {
            // Sử dụng câu truy vấn WHERE để lọc theo locationid
            String selection = DatabaseHelper.COLUMN_LOCATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(locationid)}; // Tìm kiếm nhà hàng theo location_id

            // Thực hiện truy vấn cơ sở dữ liệu
            cursor = database.query(DatabaseHelper.TABLE_RESTAURANTS, columns, selection, selectionArgs, null, null, null);

            // Duyệt qua các kết quả và thêm vào danh sách restaurantList
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                    int locationId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                    int cateId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                    int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                    int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                    // Tạo đối tượng Restaurant và thêm vào danh sách
                    restaurantList.add(new Restaurant(id, restaurantName, address, locationId, cateId, image, star,description, history));
                } while (cursor.moveToNext());
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

    public List<Restaurant> getRestaurantsByLocationName(String name) {
        List<Restaurant> restaurantList = new ArrayList<>();
        Cursor cursor = null;

        try {
            // Truy vấn địa điểm theo tên
            String locationSelection = DatabaseHelper.COLUMN_LOCATION_LOCATION_NAME + " LIKE ?";
            String[] locationSelectionArgs = {"%" + name + "%"}; // Tìm kiếm tên địa điểm gần đúng

            Cursor locationCursor = database.query(
                    DatabaseHelper.TABLE_LOCATIONS,
                    new String[]{DatabaseHelper.COLUMN_LOCATION_LOCATION_ID},
                    locationSelection,
                    locationSelectionArgs,
                    null, null, null);

            // Nếu tìm thấy địa điểm tương ứng
            if (locationCursor != null && locationCursor.moveToFirst()) {
                int locationId = locationCursor.getInt(locationCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_LOCATION_ID));


                // Truy vấn nhà hàng theo location_id
                String restaurantSelection = DatabaseHelper.COLUMN_LOCATION_ID + " = ?";
                String[] restaurantSelectionArgs = {String.valueOf(locationId)};

                cursor = database.query(
                        DatabaseHelper.TABLE_RESTAURANTS,
                        columns,
                        restaurantSelection,
                        restaurantSelectionArgs,
                        null, null, null);

                // Duyệt qua kết quả và thêm vào danh sách restaurantList
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                        String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                        String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                        int locationIdValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION_ID));
                        int cateId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATE_ID));
                        String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
                        int star = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAR));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                        int history = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY));

                        // Tạo đối tượng Restaurant và thêm vào danh sách
                        restaurantList.add(new Restaurant(id, restaurantName, address, locationIdValue, cateId, image, star,description, history));
                    } while (cursor.moveToNext());
                }
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
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, restaurant.getAddress());  // Cập nhật địa chỉ
        values.put(DatabaseHelper.COLUMN_LOCATION_ID, restaurant.getLocationid());  // Cập nhật locationid
        values.put(DatabaseHelper.COLUMN_CATE_ID, restaurant.getCateid());  // Cập nhật cateid
        values.put(DatabaseHelper.COLUMN_IMAGE, restaurant.getImage());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, restaurant.getDescription());
        values.put(DatabaseHelper.COLUMN_STAR, restaurant.getStar());  // Cập nhật đánh giá
        values.put(DatabaseHelper.COLUMN_HISTORY, restaurant.getStar());

        // Cập nhật quán ăn theo ID
        int rowsUpdated = database.update(DatabaseHelper.TABLE_RESTAURANTS, values,
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
