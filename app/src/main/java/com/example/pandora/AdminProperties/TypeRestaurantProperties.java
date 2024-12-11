package com.example.pandora.AdminProperties;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.CategoryAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TypeRestaurantProperties extends AppCompatActivity {

    RecyclerView recyclerView;
    private CatetgoryDatabase db;
    List<Category> categoryList;
    CategoryAdapter adapter;
    View dialogView;

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

        db = new CatetgoryDatabase(this); // Khởi tạo với context
        db.open();
        categoryList = db.getAllCategories();

        adapter = new CategoryAdapter(categoryList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

       adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(Category category) {
                showUpdateTypeRestaurantAlertDialog();
           }
       });

        FloatingActionButton fbAdd = findViewById(R.id.fab_add);
        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTypeRestaurantAlertDialog();
            }
        });

    }

    private void showAddTypeRestaurantAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_add_type_restaurant_admin, null);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);


        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            recreate();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void showUpdateTypeRestaurantAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_update_type_restaurant_admin, null);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        TextView deleteType = dialogView.findViewById(R.id.deleteType);

        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            recreate();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

}