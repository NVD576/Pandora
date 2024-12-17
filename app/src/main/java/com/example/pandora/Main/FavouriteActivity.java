package com.example.pandora.Main;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Favorite;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.FavoriteDatabase;
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
    List<Favorite> favoriteList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourite);

        Intent myIntent = getIntent();
        int userid = myIntent.getIntExtra("userid", -1);
        if(userid!=-1){
            FavoriteDatabase favoriteDatabase= new FavoriteDatabase(this);
            favoriteDatabase.open();
            favoriteList= favoriteDatabase.getFavoritesByUserId(userid);

            favoriteList= favoriteList.stream().filter(p->p.getLike()==1)
                    .collect(Collectors.toList());
            favoriteDatabase.close();

            RestaurantDatabase restaurantDatabase= new RestaurantDatabase(getApplicationContext());
            restaurantDatabase.open();
            restaurantList= restaurantDatabase.getAllRestaurants();

            restaurantList = restaurantList.stream()
                    .filter(restaurant -> favoriteList.stream()
                            .anyMatch(favorite -> favorite.getRestaurantid() == restaurant.getId()))
                    .collect(Collectors.toList());

            restaurantDatabase.close();
        }




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