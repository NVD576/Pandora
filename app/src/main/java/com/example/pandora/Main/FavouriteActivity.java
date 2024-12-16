package com.example.pandora.Main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Fragment.DetailRestaurantFragment;
import com.example.pandora.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavouriteActivity extends AppCompatActivity {
    List<Restaurant> restaurantList= new ArrayList<>();
    RestaurantAdapter smaillRestaurantAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourite);
        RestaurantDatabase restaurantDatabase= new RestaurantDatabase(getApplicationContext());
        restaurantDatabase.open();
        restaurantList= restaurantDatabase.getAllRestaurants();
        restaurantList=restaurantList.stream().filter(p->p.getHistory()==1).collect(Collectors.toList());
        restaurantDatabase.close();



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        smaillRestaurantAdapter = new RestaurantAdapter(this,restaurantList);
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