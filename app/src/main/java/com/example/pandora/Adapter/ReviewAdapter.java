package com.example.pandora.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Review;
import com.example.pandora.Class.User;
import com.example.pandora.Database.RatingDatabase;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviewList;
    private OnItemClickListener listener;
    Context context;

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }


    public void setFilteredList(List<Review> filteredList) {
        this.reviewList = filteredList;
        notifyDataSetChanged();
    }
    // Định nghĩa interface OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(Review review);
    }

    // Hàm để thiết lập listener từ bên ngoài
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        UserDatabase db = new UserDatabase(holder.itemView.getContext());
        db.open();
        User u = db.getUserById(review.getUserid());
        db.close();
        String name = u.getName();
        if (u.getName().isEmpty()) {
            holder.userNameTextView.setText("User");
        } else {
            holder.userNameTextView.setText(name);
        }

        if (u.getImage() != null && !u.getImage().isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(u.getImage());
            holder.userImage.setImageBitmap(bitmap);
        } else {
            holder.userImage.setImageResource(R.drawable.person_icon); // Đặt ảnh mặc định nếu không tải được ảnh
        }
        holder.commentTextView.setText(review.getReview());
        holder.dateTextView.setText(review.getDate());

        RatingDatabase ratingDatabase= new RatingDatabase(context);
        ratingDatabase.open();
        int ra= ratingDatabase.getRatingById(u.getId(),review.getRestaurantid()).getStar();
        holder.starNumber.setText(String.valueOf(ra));
        ratingDatabase.close();



        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(review);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, commentTextView, dateTextView,starNumber;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            starNumber = itemView.findViewById(R.id.starNumber);
            userNameTextView = itemView.findViewById(R.id.userTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    private Bitmap loadImageFromInternalStorage(String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            return BitmapFactory.decodeStream(fis); // Chuyển đổi thành Bitmap
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu không tìm thấy ảnh
        }
    }
}
