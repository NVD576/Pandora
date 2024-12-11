package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

public class AdminProperties extends AppCompatActivity {
    int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        UserDatabase db = new UserDatabase(this);
        db.open();
        User user = db.getUserById(userid);
        db.close();

        TextView btnListRestaurant = findViewById(R.id.listRestaurant);
        TextView btnListReview = findViewById(R.id.listReview);
        TextView btnListUser = findViewById(R.id.listUser);
        TextView btnListCategory = findViewById(R.id.listCategory);

        //Hiện quyền
        if (user.isRoleUser()) btnListUser.setVisibility(View.VISIBLE);
        if (user.isRoleCategory()) btnListCategory.setVisibility(View.VISIBLE);
        if (user.isRoleRestaurant()) btnListRestaurant.setVisibility(View.VISIBLE);
        if (user.isRoleReview()) btnListReview.setVisibility(View.VISIBLE);

        btnListUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, AccountProperties.class);
                startActivity(myIntent);
            }
        });
        btnListRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, RestaurantProperties.class);
                startActivity(myIntent);
            }
        });
        btnListReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, ListReviewProperties.class);
                startActivity(myIntent);
            }
        });
        btnListCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, TypeRestaurantProperties.class);
                startActivity(myIntent);
            }
        });

    }
}