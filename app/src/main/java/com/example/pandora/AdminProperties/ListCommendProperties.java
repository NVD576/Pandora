package com.example.pandora.AdminProperties;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.ReviewAdapter;
import com.example.pandora.Class.Review;
import com.example.pandora.Database.ReviewDatabase;
import com.example.pandora.R;

import java.util.List;

public class ListCommendProperties extends AppCompatActivity {

    private String restaurantName;
    private int star, id;
    private ReviewAdapter adapter;
    private ReviewDatabase db;
    private RecyclerView recyclerView;
    private List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_commend_properties);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurant_name");
            star = intent.getIntExtra("restaurant_rating", 0);
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
}
