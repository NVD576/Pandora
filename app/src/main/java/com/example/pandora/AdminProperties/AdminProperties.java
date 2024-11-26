package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.R;

import org.w3c.dom.Text;

public class AdminProperties extends AppCompatActivity {

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

        TextView btnListUser = findViewById(R.id.listUser);
        btnListUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, AccountProperties.class);
                startActivity(myIntent);
            }
        });
        TextView btnListRestaurant = findViewById(R.id.listRestaurant);
        btnListRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, RestaurantProperties.class);
                startActivity(myIntent);
            }
        });
        TextView btnDecentralization = findViewById(R.id.Decentralization);
        btnDecentralization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AdminProperties.this, RoleProperties.class);
                startActivity(myIntent);
            }
        });
    }
}