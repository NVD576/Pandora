package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.ReviewAdapter;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.Review;
import com.example.pandora.Class.User;
import com.example.pandora.Database.ReviewDatabase;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

import java.util.ArrayList;
import java.util.List;

public class ListCommendProperties extends AppCompatActivity {

    private String restaurantName;
    private int  id;
    private ReviewAdapter adapter;
    private ReviewDatabase db;
    private RecyclerView recyclerView;
    private List<Review> reviewList;
    private Handler handler = new Handler();
    private Runnable searchRunnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_commend_properties);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurant_name");
            id = intent.getIntExtra("restaurant_id", 0);
        }

        // Thiết lập Database
        db = new ReviewDatabase(getApplicationContext());
        db.open();
        reviewList = db.getReviewsByRestaurantId(id);

        // Kiểm tra danh sách review
        if (reviewList == null || reviewList.isEmpty()) {
            Toast.makeText(this, "No reviews found for this restaurant.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thiết lập RecyclerView
        adapter = new ReviewAdapter(reviewList, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Sự kiện click item
        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Review review) {
                showDialogUpdateReviewAdmin(review);
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

    }

    public void showDialogUpdateReviewAdmin(Review review) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_review_restaurant_admin, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Xử lý nút Delete
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteReview(review.getId());
                reviewList.remove(review); // Cập nhật danh sách
                adapter.notifyDataSetChanged(); // Làm mới RecyclerView
                Toast.makeText(ListCommendProperties.this, "Review deleted successfully.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        // Xử lý nút Dismiss
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
    private void filterList(String query) {
        if (reviewList == null || reviewList.isEmpty()) {
            return;
        }

        UserDatabase userDatabase= new UserDatabase(this);
        userDatabase.open();


        List<Review> filteredList = new ArrayList<>();
        for (Review review : reviewList) {
            // Kiểm tra xem tên người dùng và bình luận có chứa chuỗi tìm kiếm không
            User user= userDatabase.getUserById(review.getUserid());
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    review.getReview().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(review);
            }
        }
        userDatabase.close();
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật lại danh sách lọc cho adapter
            adapter.setFilteredList(filteredList);
        }
    }


}
