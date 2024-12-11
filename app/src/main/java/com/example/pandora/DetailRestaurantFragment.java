package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.ReviewAdapter;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Rating;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.Review;
import com.example.pandora.Class.User;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RatingDatabase;
import com.example.pandora.Database.RestaurantDatabase;
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
    boolean isLogin = false;
    EditText txtComment;
    int restaurant_id;
    User u;
    Review review;
    List<Review> commentsList;
    ReviewAdapter reviewAdapter;
    TextView txtLocation;
    RestaurantDatabase restaurantDatabase;
    private RecyclerView commentsRecyclerView;
    RatingDatabase ratingDatabase ;
    ReviewDatabase reviewDatabase ;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Khởi tạo cơ sở dữ liệu sau khi Fragment đã gắn kết với Activity
        restaurantDatabase = new RestaurantDatabase(context);
        ratingDatabase = new RatingDatabase(context);
        reviewDatabase = new ReviewDatabase(context);
    }
    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);

        TextView nameTextView = view.findViewById(R.id.detailName);
        RatingBar ratingBar = view.findViewById(R.id.detailRating);
        TextView detailRatingRes = view.findViewById(R.id.detailRatingRes);
        txtLocation = view.findViewById(R.id.txtLocation);
        Rating rating=null;
        // Lấy thông tin đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();

        restaurant_id = bundle.getInt("restaurant_id");

        // Lấy thông tin nhà hàng
        restaurantDatabase.open();
        Restaurant restaurant = restaurantDatabase.getRestaurantsByID(restaurant_id);
        restaurant.setHistory(1);
        // Lấy thông tin vị trí
        LocationDatabase lD = new LocationDatabase(getContext());
        lD.open();
        Location l = lD.getLocationById(restaurant.getLocationid());
        lD.close();

        // Lấy và xử lý thông tin đánh giá

        ratingDatabase.open();
        rating = ratingDatabase.getRatingById(userid, restaurant_id);
        if (rating == null) {
            rating = new Rating(userid, restaurant_id, 0);
        }

        // Cập nhật điểm trung bình của nhà hàng
        restaurant.setStar(ratingDatabase.getAverageRating(restaurant_id));
        restaurantDatabase.updateRestaurant(restaurant);

        txtLocation.setText(l.getName() + ", " + restaurant.getAddress());
        detailRatingRes.setText(String.valueOf(restaurant.getStar()));

        // Lắng nghe sự thay đổi điểm đánh giá
        Rating finalRating = rating;
        ratingBar.setOnRatingBarChangeListener((ratingBar1, r, fromUser) -> {
            if (fromUser && isLogin) {
                // Lưu giá trị rating vào cơ sở dữ liệu
                finalRating.setStar((int) r);
                if (finalRating.getId() > 0) {
                    ratingDatabase.updateRating(userid, restaurant_id, (int) r);
                } else {
                    ratingDatabase.addRating(finalRating);
                }

                // Cập nhật lại điểm trung bình của nhà hàng
                restaurant.setStar(ratingDatabase.getAverageRating(restaurant_id));

                restaurantDatabase.updateRestaurant(restaurant);


                // Cập nhật giao diện
                detailRatingRes.setText(String.valueOf(restaurant.getStar()));
                ratingBar.setRating(r);
            }
        });

        ratingBar.setRating(rating.getStar());
        nameTextView.setText(bundle.getString("restaurant_name", "N/A"));




        // Lấy danh sách bình luận

        reviewDatabase.open();
        commentsList = reviewDatabase.getReviewsByRestaurantId(restaurant_id);


        // Nếu không có bình luận, khởi tạo một danh sách rỗng
        if (commentsList.isEmpty()) {
            commentsList = new ArrayList<>();
        }

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        reviewAdapter = new ReviewAdapter(commentsList, getContext());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(reviewAdapter);

        if (isLogin) {
            UserDatabase db = new UserDatabase(getContext());
            db.open();
            u = db.getUserById(userid);
            db.close();
        }

        // Xử lý gửi bình luận
        btnSend = view.findViewById(R.id.btnSend);
        txtComment = view.findViewById(R.id.txtComment);
        btnSend.setOnClickListener(v -> {
            String newComment = txtComment.getText().toString().trim();
            if (!newComment.isEmpty() && isLogin) {
                String currentTime = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
                review = new Review(userid, restaurant_id, newComment, currentTime);

                // Thêm bình luận vào danh sách
                commentsList.add(review);
                reviewAdapter.notifyDataSetChanged();
                txtComment.setText("");

                // Lưu bình luận vào cơ sở dữ liệu
                saveCommentToDatabase(review);
            } else if (!isLogin) {
                Toast.makeText(getContext(), "Cần đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Bình luận không thể trống", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    public void onDestroyView() {
        super.onDestroyView();
        // Đóng cơ sở dữ liệu ở đây khi người dùng thoát trang
        restaurantDatabase.close();

        ratingDatabase.close();

        reviewDatabase.close();
    }
    // Lưu bình luận vào cơ sở dữ liệu
    private void saveCommentToDatabase(Review rv) {
        ReviewDatabase db = new ReviewDatabase(getContext());
        db.open();
        db.addReview(rv);
        db.close();
    }
}

