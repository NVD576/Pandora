package com.example.pandora.AdminProperties;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.CategoryAdapter;
import com.example.pandora.Adapter.LocationAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Class.Location;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TypeRestaurantProperties extends AppCompatActivity {

    RecyclerView recyclerView;
    private CatetgoryDatabase db;
    LocationDatabase locationDatabase;
    List<Category> categoryList;
    List<Location> locationList;
    CategoryAdapter adapter;
    LocationAdapter locationAdapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    Button btnLocation, btntypeRestaurant;
    View dialogView;
    boolean isLocation=false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_restaurant_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //nut cac dia diem
        btnLocation=findViewById(R.id.btnLocation);
        btntypeRestaurant=findViewById(R.id.btnTypeRestaurant);

        locationDatabase = new LocationDatabase(this);
        locationDatabase.open();
        locationList = locationDatabase.getAllLocations();

        db = new CatetgoryDatabase(this); // Khởi tạo với context
        db.open();
        categoryList = db.getAllCategories();

        adapter = new CategoryAdapter(categoryList);
        locationAdapter = new LocationAdapter(locationList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (!isLocation) {

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Category category) {
                    showUpdateTypeRestaurantAlertDialog(category);
                }
            });

        }

        // nut cong
        FloatingActionButton fbAdd = findViewById(R.id.fab_add);
        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTypeRestaurantAlertDialog();
            }
        });


        //thanh search
        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the list as the text changes
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                // Đặt Runnable mới với độ trễ 2 giây
                searchRunnable = () -> filterList(charSequence.toString());
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLocation.setBackgroundResource(R.drawable.button_selected_home_background);
                btntypeRestaurant.setBackgroundResource(R.drawable.button_unselected_home_background);
                isLocation = true;


                recyclerView.setAdapter(locationAdapter);
                locationAdapter.notifyDataSetChanged();
                locationAdapter.setOnItemClickListener(location ->
                        showUpdateTypeRestaurantAlertDialog(location));

            }
        });
        //nut loai quan an

        btntypeRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnLocation.setBackgroundResource(R.drawable.button_unselected_home_background);
                btntypeRestaurant.setBackgroundResource(R.drawable.button_selected_home_background);
                isLocation = false;
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo đóng database khi Activity bị hủy
        if (db != null) {
            db.close();
        }
        if (locationDatabase != null)
            locationDatabase.close();

    }


    private void filterList(String query) {
        if(!isLocation){
            if (categoryList == null || categoryList.isEmpty()) {
                return;
            }

            List<Category> filteredList = new ArrayList<>();
            for (Category category : categoryList) {
                if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(category);
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                adapter.setFilteredList(filteredList);

            }
        }else{
            if (locationList == null || locationList.isEmpty()) {
                return;
            }

            List<Location> filteredList = new ArrayList<>();
            for (Location location : locationList) {
                if (location.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(location);
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                locationAdapter.setFilteredList(filteredList);

            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddTypeRestaurantAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_add_type_restaurant_admin, null);

        EditText addTypeName = dialogView.findViewById(R.id.addTypeName);


        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);



        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {


            if(isLocation){

                Location l = new Location(addTypeName.getText().toString());
                if (addTypeName.getText().toString() == null)
                    Toast.makeText(this, "Nhap thong tin!", Toast.LENGTH_SHORT).show();
                else {
                    locationDatabase.addLocation(l);
                    locationList.clear();
                    locationList.addAll(locationDatabase.getAllLocations());
                    locationAdapter.notifyDataSetChanged();
//                    onResume();
                }
            }else{
                Category c = new Category(addTypeName.getText().toString());
                if (addTypeName.getText().toString() == null)
                    Toast.makeText(this, "Nhap thong tin!", Toast.LENGTH_SHORT).show();
                else {

                    db.addCategory(c);
                    adapter.notifyDataSetChanged();
//                    onResume();
                    recreate();
                }
            }

            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void showUpdateTypeRestaurantAlertDialog(Category category) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_update_type_restaurant_admin, null);

        TextView deleteType = dialogView.findViewById(R.id.deleteType);
        EditText updateTypeName = dialogView.findViewById(R.id.updateTypeName);
        updateTypeName.setText(category.getName());

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();
        Toast.makeText(getApplicationContext(),String.valueOf(category.getId()),Toast.LENGTH_SHORT).show();
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);

        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            category.setName(updateTypeName.getText().toString());
            db.updateCategory(category);
            adapter.notifyDataSetChanged();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        //Xu ly nut xoa
        deleteType.setOnClickListener(view -> {
//            int position = categoryList.indexOf(category);
            db.deleteCategory(category.getId());
            recreate();
        });

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void showUpdateTypeRestaurantAlertDialog(Location location) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_update_type_restaurant_admin, null);

        TextView deleteType = dialogView.findViewById(R.id.deleteType);
        EditText updateTypeName = dialogView.findViewById(R.id.updateTypeName);
        updateTypeName.setText(location.getName());
        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);

        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            location.setName(updateTypeName.getText().toString());
            locationDatabase.updateLocation(location);
            locationAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
//            recreate();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        //Xu ly nut xoa
        deleteType.setOnClickListener(view -> {
            int position = locationList.indexOf(location); // Lấy vị trí của item trong danh sách
            if (position != -1) { // Kiểm tra nếu item tồn tại trong danh sách
                locationDatabase.deleteLocation(location.getId()); // Xóa item khỏi database
                locationList.remove(position); // Xóa item khỏi danh sách// Cập nhật RecyclerView
                locationAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });


        // Hiển thị hộp thoại
        alertDialog.show();
    }


}