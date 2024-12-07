package com.example.pandora.Main;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.R;

import java.util.ArrayList;
import java.util.List;

public class SearchInfo extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Restaurant> restaurantList;
    private RestaurantAdapter adapter;
    private EditText searchTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_info);

        // Initialize the restaurant list
        restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant("A", 1, 4));
        restaurantList.add(new Restaurant("B", 1, 1));
        restaurantList.add(new Restaurant("C", 2, 4));
        restaurantList.add(new Restaurant("D", 2, 2));

        // Set up the EditText search tool
        searchTool = findViewById(R.id.search_toolbar);
        searchTool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the list as the text changes
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed in this case
            }
        });

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RestaurantAdapter(restaurantList);
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            EditText searchEditText = findViewById(R.id.search_toolbar);
            searchEditText.requestFocus();

            // Mở bàn phím
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
