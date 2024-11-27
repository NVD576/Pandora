package com.example.pandora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Restaurant;

import java.util.List;

class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ReviewViewHolder> {

    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurantList = restaurants;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.reviewTextView.setText(restaurant.getReview());
        holder.imageView.setImageResource(restaurant.getImageResId());
        holder.ratingBar.setRating(restaurant.getStart());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView reviewTextView;
        ImageView imageView;
        RatingBar ratingBar;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.reviewName);
            reviewTextView = itemView.findViewById(R.id.reviewText);
            imageView = itemView.findViewById(R.id.reviewImage);
            ratingBar = itemView.findViewById(R.id.reviewRating);
        }
    }
}
