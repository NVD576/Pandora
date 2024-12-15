package com.example.pandora.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.SmaillRestaurantAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.DetailRestaurantFragment;
import com.example.pandora.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveLocationReview extends AppCompatActivity {

    List<Restaurant> restaurantList= new ArrayList<>();
    SmaillRestaurantAdapter smaillRestaurantAdapter;
    RecyclerView recyclerView;
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
        smaillRestaurantAdapter = new SmaillRestaurantAdapter(this,restaurantList);
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

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SaveLocationReview.this, Lobby.class);
                startActivity(myIntent);
            }
        });

    }
}