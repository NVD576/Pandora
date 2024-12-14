package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListReviewProperties extends AppCompatActivity {


    RestaurantAdapter adapter;
    RestaurantDatabase db;
    RecyclerView recyclerView;
    List<Restaurant> restaurantList;
    private Handler handler = new Handler();
    private Runnable searchRunnable;


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


        FloatingActionButton fabAdd = findViewById(R.id.fab_chart);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ListReviewProperties.this, ThongKe.class);
                startActivity(myIntent);
            }
        });
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

}