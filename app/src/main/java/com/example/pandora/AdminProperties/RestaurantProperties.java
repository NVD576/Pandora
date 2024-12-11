package com.example.pandora.AdminProperties;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.User;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.C;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RestaurantProperties extends AppCompatActivity {

    RecyclerView recyclerView;
    private RestaurantDatabase db;
    List<Restaurant> restaurantList;
    RestaurantAdapter adapter;
    CatetgoryDatabase catetgoryDatabase;
    LocationDatabase locationDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    Restaurant restaurant;
    View dialogView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_properties);


        db = new RestaurantDatabase(this); // Khởi tạo với context
        db.open();
        restaurantList = db.getAllRestaurants();


        adapter = new RestaurantAdapter(restaurantList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the list as the text changes
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRestaurantAlertDialog();
            }
        });

    }

    private void showAddRestaurantAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_add_restaurant_admin, null);

        EditText addRestaurantName= dialogView.findViewById(R.id.addRestaurantName);
        EditText addAddress= dialogView.findViewById(R.id.addAddress);
        EditText addDescription= dialogView.findViewById(R.id.addDescription);
        Button btnChooseImage=dialogView.findViewById(R.id.btnChooseImage);

        restaurant = new Restaurant();

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();


        // location
        locationDatabase = new LocationDatabase(this);
        locationDatabase.open();
        List<Location> lc = locationDatabase.getAllLocations();



        List<String> items = new ArrayList<>();
        items.add("Chọn thành phố");
        for (Location l : lc) {
            items.add(l.getName());
        }

        Spinner addLocation = dialogView.findViewById(R.id.addLocation);
        ArrayAdapter<String> aa = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                items);

//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
//        );
        addLocation.setAdapter(aa);
        addLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               spinner1.setText(items[position]);
                if (items.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setLocationid(position);

                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //type of restaurant

        catetgoryDatabase = new CatetgoryDatabase(this);
        catetgoryDatabase.open();
        List<Category> categoryList = catetgoryDatabase.getAllCategories();


        List<String> item1 = new ArrayList<>();
        for (Category l : categoryList) {
            item1.add(l.getName());
        }

        Spinner addRoleRestaurant = dialogView.findViewById(R.id.addRoleRestaurant);
        ArrayAdapter<String> bb = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                item1);

//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
//        );
        addRoleRestaurant.setAdapter(bb);
        addRoleRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               spinner1.setText(items[position]);
                if (item1.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setCateid(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //button chon image
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });




        // Tìm các thành phần trong layout dialog
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);


        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            restaurant.setName(addRestaurantName.getText().toString());
            restaurant.setAddress(addAddress.getText().toString());
            restaurant.setDescription(addDescription.getText().toString());
            db.addRestaurant(restaurant);

            adapter.updateData(restaurantList);
            recreate();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void filterList(String query) {
        if (restaurantList == null || restaurantList.isEmpty()) {
            return;
        }

        List<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filteredList);

        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo đóng database khi Activity bị hủy
        if (db != null) {
            db.close();
        }
        if(locationDatabase!=null)
            locationDatabase.close();
        if(catetgoryDatabase!=null)
            catetgoryDatabase.close();
    }

    //lưu ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileName="";
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh được chọn
            Uri selectedImageUri = data.getData();

            // Lấy đường dẫn thực tế từ URI
            try {
                // Chuyển đổi URI sang Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                Calendar calendar = Calendar.getInstance(); // Khởi tạo Calendar
                long timestamp = calendar.getTimeInMillis(); // Lấy thời gian hiện tại dưới dạng milliseconds

                // Tạo tên file duy nhất
                fileName = "restaurant_image_" + timestamp + ".png";

                // Cập nhật vào database
                restaurant.setImage(fileName);
                // Lưu Bitmap vào bộ nhớ trong
                saveImageToInternalStorage(bitmap, fileName);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Cập nhật ngay trong ImageView
            TextView textImage= dialogView.findViewById(R.id.textImage);
            textImage.setText(fileName);

            // Thông báo thành công
            Toast.makeText(this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            // Lưu ảnh vào bộ nhớ trong
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e("SaveImage", "Ảnh đã được lưu vào bộ nhớ trong: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadImageFromInternalStorage(String fileName) {
        try {
            FileInputStream fis = openFileInput(fileName);
            return BitmapFactory.decodeStream(fis); // Chuyển đổi thành Bitmap
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu không tìm thấy ảnh
        }
    }



}