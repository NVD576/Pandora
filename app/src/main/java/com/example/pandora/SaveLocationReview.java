package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Adapter.SmaillRestaurantAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.RestaurantDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveLocationReview extends AppCompatActivity {

    List<Restaurant> restaurantList= new ArrayList<>();
    SmaillRestaurantAdapter smaillRestaurantAdapter;
    RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_location_review);
        RestaurantDatabase restaurantDatabase= new RestaurantDatabase(getApplicationContext());
        restaurantDatabase.open();

        Intent myIntent = getIntent();
        ArrayList<Integer> a = myIntent.getIntegerArrayListExtra("history");
        if(a!=null){
            for(int i : a){
                Restaurant rs= restaurantDatabase.getRestaurantsByID(i);
                restaurantList.add(rs);
            }
            Collections.reverse(restaurantList);
        }

        restaurantDatabase.close();



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        smaillRestaurantAdapter = new SmaillRestaurantAdapter(restaurantList);
        recyclerView.setAdapter(smaillRestaurantAdapter);
        smaillRestaurantAdapter.notifyDataSetChanged();
        // Trong Activity hoặc Fragment nơi bạn sử dụng RecyclerView
        smaillRestaurantAdapter.setOnItemClickListener(restaurant -> {
            DetailRestaurantFragment nextFragment = new DetailRestaurantFragment();

            // Truyền dữ liệu qua Bundle
            Bundle bundle = new Bundle();
            bundle.putString("restaurant_name", restaurant.getName());
            bundle.putInt("restaurant_rating", restaurant.getStar());
            bundle.putInt("restaurant_id", restaurant.getId());
            nextFragment.setArguments(bundle);

            // Chuyển Fragment
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,  // Khi fragment xuất hiện
                            R.anim.fade_out    // Khi fragment rời đi
                    )
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit();
        });

    }
}