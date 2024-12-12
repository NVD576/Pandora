package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.R;

import java.util.List;

public class ListReviewProperties extends AppCompatActivity {


    RestaurantAdapter adapter;
    RestaurantDatabase db;
    RecyclerView recyclerView;
    List<Restaurant> restaurantList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_rating_commend_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new RestaurantDatabase(getApplicationContext());
        db.open();
        restaurantList = db.getAllRestaurants();
        db.close();

        adapter = new RestaurantAdapter(this, restaurantList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                Intent myIntent = new Intent(ListReviewProperties.this, ListCommendProperties.class);
                myIntent.putExtra("restaurant_name", restaurant.getName());
                myIntent.putExtra("restaurant_rating", restaurant.getStar());
                myIntent.putExtra("restaurant_id", restaurant.getId());
                startActivity(myIntent);
            }
        });
    }
}