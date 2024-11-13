package com.example.pandora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Restaurant> restaurantList;

    public ReviewAdapter(List<Restaurant> restaurants) {
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
