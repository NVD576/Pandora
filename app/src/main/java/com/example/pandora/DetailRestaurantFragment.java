package com.example.pandora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailRestaurantFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);

        TextView nameTextView = view.findViewById(R.id.detailName);
        TextView reviewTextView = view.findViewById(R.id.detailReview);
        RatingBar ratingBar = view.findViewById(R.id.detailRating);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameTextView.setText(bundle.getString("restaurant_name", "N/A"));
            reviewTextView.setText(bundle.getString("restaurant_review", "N/A"));
            ratingBar.setRating(bundle.getInt("restaurant_rating", 0));
        }

        return view;
    }
}
