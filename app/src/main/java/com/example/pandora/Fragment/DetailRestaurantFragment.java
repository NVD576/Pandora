package com.example.pandora.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.pandora.Class.Favorite;
import com.example.pandora.Class.Image;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Rating;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.Review;
import com.example.pandora.Class.User;
import com.example.pandora.Database.FavoriteDatabase;
import com.example.pandora.Database.ImageDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RatingDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Database.ReviewDatabase;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailRestaurantFragment extends Fragment {

    ImageView btnSend,restaurantImage,favouriteCheck;
    int userid,restaurant_id;
    boolean isLogin = false;
    EditText txtComment;
    User u;
    Review review;
    List<Review> commentsList;
    ReviewAdapter reviewAdapter;
    TextView txtLocation,txtDescription, ratingOver,nameTextView;
    RestaurantDatabase restaurantDatabase;
    FavoriteDatabase favoriteDatabase;
    private RecyclerView commentsRecyclerView;
    RatingDatabase ratingDatabase ;
    ReviewDatabase reviewDatabase ;
    Rating rating;
    ImageDatabase imageDatabase;
    LocationDatabase lD ;
    Favorite favorite;
    RatingBar ratingBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Khởi tạo cơ sở dữ liệu sau khi Fragment đã gắn kết với Activity
        restaurantDatabase = new RestaurantDatabase(context);
        ratingDatabase = new RatingDatabase(context);
        reviewDatabase = new ReviewDatabase(context);
    }
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);

        // Lấy thông tin đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        nameTextView = view.findViewById(R.id.detailName);
        ratingBar = view.findViewById(R.id.detailRating);

        txtLocation = view.findViewById(R.id.txtLocation);
        favouriteCheck=view.findViewById(R.id.favouriteCheck);
        txtDescription= view.findViewById(R.id.txtDescription);
        ratingOver= view.findViewById(R.id.ratingOver);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        restaurantImage= view.findViewById(R.id.restaurantImage);
        restaurant_id = bundle.getInt("restaurant_id");
        // Lấy thông tin nhà hàng
        restaurantDatabase.open();
        Restaurant restaurant = restaurantDatabase.getRestaurantsByID(restaurant_id);
//        restaurant.setHistory(1);
        // Lấy thông tin vị trí
        lD = new LocationDatabase(getContext());
        lD.open();
        Location l = lD.getLocationById(restaurant.getLocationid());
        imageDatabase= new ImageDatabase(getContext());
        favoriteDatabase= new FavoriteDatabase(getContext());
        favoriteDatabase.open();



        // Lấy và xử lý thông tin đánh giá
        ratingDatabase.open();
        txtDescription.setText(restaurant.getDescription());

        if (isLogin){
            favorite= favoriteDatabase.getFavoriteById(userid, restaurant_id);
            if(favorite==null){
                favorite=new Favorite(restaurant_id, userid,0);
                favoriteDatabase.addFavorite(favorite);
            }


            rating = ratingDatabase.getRatingById(userid, restaurant_id);
            if (rating == null) {
                rating = new Rating(userid, restaurant_id, 0);
            }
            if(favorite.getLike()==1){
                favouriteCheck.setImageResource(R.drawable.favourite_red_icon);
            }
            favouriteCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favorite.getLike()==1){
                        favorite.setLike(0);
                        favouriteCheck.setImageResource(R.drawable.unfavourite_red_icon);
                    }
                    else{
                        favorite.setLike(1);
                        favouriteCheck.setImageResource(R.drawable.favourite_red_icon);
                    }
                    favoriteDatabase.updateFavorite(favorite);
                    onResume();
                }
            });
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
                    ratingOver.setText("Đánh giá: "+restaurant.getStar()+" ");

                    restaurantDatabase.updateRestaurant(restaurant);
                    reviewAdapter.notifyDataSetChanged();


                    // Cập nhật giao diện
                    ratingBar.setRating(r);
                }
            });
            ratingBar.setRating(rating.getStar());


        }

        //hien rating
        nameTextView.setText(restaurant.getName());
        ratingOver.setText("Đánh giá: "+restaurant.getStar()+" ");
        // Cập nhật điểm trung bình của nhà hàng
        restaurant.setStar(ratingDatabase.getAverageRating(restaurant_id));
        restaurantDatabase.updateRestaurant(restaurant);
        //hien dia diem
        txtLocation.setText(l.getName() + ", " + restaurant.getAddress());

        //hien image
        imageDatabase.open();
        List<Image> imageList= imageDatabase.getImageByRestaurantId(restaurant_id);
        imageDatabase.close();

        if (!imageList.isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(imageList.get(0).getImageUrl());
            if (bitmap != null) {
                restaurantImage.setImageBitmap(bitmap);
            }
        }

        // Lấy danh sách bình luận
        reviewDatabase.open();
        commentsList = reviewDatabase.getReviewsByRestaurantId(restaurant_id);


        // Nếu không có bình luận, khởi tạo một danh sách rỗng
        if (commentsList.isEmpty()) {
            commentsList = new ArrayList<>();
        }

        //hien binh luan-comment
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
            if (!newComment.isEmpty() && isLogin &&ratingBar.getRating()>0) {
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
            } else if(ratingBar.getRating()<1) {
                Toast.makeText(getContext(), "Cần đánh giá trước", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Bình luận không thể trống", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView image1 = view.findViewById(R.id.image1);
        ImageView image2 = view.findViewById(R.id.image2);
        ImageView image3 = view.findViewById(R.id.image3);
        ImageView image4 = view.findViewById(R.id.image4);

        if(!imageList.isEmpty()){
            Bitmap bitmap = loadImageFromInternalStorage(imageList.get(0).getImageUrl());
            if (bitmap != null) {
                image1.setImageBitmap(bitmap);
            }
        }
        if(imageList.size()>1){
            Bitmap bitmap = loadImageFromInternalStorage(imageList.get(1).getImageUrl());
            if (bitmap != null) {
                image2.setImageBitmap(bitmap);
            }
        }else image2.setVisibility(View.GONE);
        if(imageList.size()>2){
            Bitmap bitmap = loadImageFromInternalStorage(imageList.get(2).getImageUrl());
            if (bitmap != null) {
                image3.setImageBitmap(bitmap);
            }
        }else image3.setVisibility(View.GONE);
        if(imageList.size()>3){
            Bitmap bitmap = loadImageFromInternalStorage(imageList.get(3).getImageUrl());
            if (bitmap != null) {
                image4.setImageBitmap(bitmap);
            }
        } else image4.setVisibility(View.GONE);



        // Set sự kiện click cho từng ảnh
        setImageClickListener(image1);
        setImageClickListener(image2);
        setImageClickListener(image3);
        setImageClickListener(image4);

        return view;
    }

    private void setImageClickListener(ImageView imageView) {
        imageView.setOnClickListener(v -> {
            // Hiển thị Dialog
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            showFullscreenImage(bitmap);
        });
    }

    private void showFullscreenImage(Bitmap bitmap) {
        // Tạo dialog
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        // Set ảnh vào ImageView trong dialog
        ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
        fullscreenImageView.setImageBitmap(bitmap);

        // Nút thoát
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
        // Hiển thị dialog
        dialog.show();
    }

    private Bitmap loadImageFromInternalStorage(String fileName) {
        try {
            FileInputStream fis = getContext().openFileInput(fileName);
            return BitmapFactory.decodeStream(fis); // Chuyển đổi thành Bitmap
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu không tìm thấy ảnh
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        // Đóng cơ sở dữ liệu ở đây khi người dùng thoát trang
        restaurantDatabase.close();

        ratingDatabase.close();
        lD.close();
        favoriteDatabase.close();
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

