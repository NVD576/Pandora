package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.ReviewAdapter;
import com.example.pandora.Class.Review;
import com.example.pandora.Class.User;
import com.example.pandora.Database.ReviewDatabase;
import com.example.pandora.Database.UserDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailRestaurantFragment extends Fragment {

    ImageView btnSend;
    int userid;
    boolean isLogin= false;
    EditText txtComment;
    int restaurant_id;
    User u;
    Review review;
    List<Review> commentsList;
    ReviewAdapter reviewAdapter;
    private RecyclerView commentsRecyclerView;
    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);

        TextView nameTextView = view.findViewById(R.id.detailName);
        RatingBar ratingBar = view.findViewById(R.id.detailRating);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameTextView.setText(bundle.getString("restaurant_name", "N/A"));
            ratingBar.setRating(bundle.getInt("restaurant_rating", 0));
            restaurant_id= bundle.getInt("restaurant_id");

        }
        Log.e("sass", "userid: " +userid);
        ReviewDatabase reviewDatabase = new ReviewDatabase(getContext());
        reviewDatabase.open();
        commentsList= reviewDatabase.getReviewsByRestaurantId(restaurant_id);
        reviewDatabase.close();

        if(commentsList.isEmpty()){
            commentsList= new ArrayList<>();
        }else{
            commentsList.size();
//            Log.e("sass", "userid: " + commentsList.get(0).getUserid() + " name: " );
            System.out.println("Sgssssgs "+"size: "+commentsList.size());
        }
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        reviewAdapter = new ReviewAdapter(commentsList, getContext());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(reviewAdapter);
        if (isLogin){
            UserDatabase db = new UserDatabase(getContext());
            db.open();
            u = db.getUserById(userid);
            db.close();
        }
        btnSend = view.findViewById(R.id.btnSend);
        txtComment= view.findViewById(R.id.txtComment);

        // Handle comment submission
        btnSend.setOnClickListener(v -> {
            String newComment = txtComment.getText().toString().trim();
            if (!newComment.isEmpty()&& isLogin) {
                String currentTime = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());

                // Thêm bình luận vào danh sách
                review= new Review(userid,restaurant_id,newComment, currentTime);
                commentsList.add(review);
                reviewAdapter.notifyDataSetChanged();
                txtComment.setText("");
//                rs.setUserComments(commentsList);
                // Lưu bình luận vào cơ sở dữ liệu (nếu cần)
                saveCommentToDatabase(review);
            }
        });


        return view;
    }
    private void saveCommentToDatabase(Review rv) {
        // Triển khai lưu bình luận vào cơ sở dữ liệu (SQLite, Firebase, v.v.)
        ReviewDatabase db = new ReviewDatabase(getContext());
        db.open();
        db.addReview(rv);
        db.close();

    }
}
