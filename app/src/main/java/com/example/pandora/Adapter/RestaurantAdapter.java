package com.example.pandora.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Image;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.ImageDatabase;
import com.example.pandora.Database.RatingDatabase;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;
    private Context context;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public void setFilteredList(List<Restaurant> filteredList) {
        this.restaurantList = filteredList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.restaurantList = restaurants;
        this.context = context;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getAddress());
        RatingDatabase ratingDatabase= new RatingDatabase(context);
        ratingDatabase.open();
        restaurant.setStar(ratingDatabase.getAverageRating(restaurant.getId()));
        ratingDatabase.close();
        holder.ratingTextView.setText(String.valueOf(restaurant.getStar()));
//        holder.imageView.setImageResource(restaurant.getImage()); // Hiển thị ảnhf
        ImageDatabase imageDatabase= new ImageDatabase(context);
        imageDatabase.open();
        List<Image> image= imageDatabase.getImageByRestaurantId(restaurant.getId());

        imageDatabase.close();
        if (!image.isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(image.get(0).getImageUrl().toString());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            }
        } else {

            holder.imageView.setImageResource(R.drawable.res1); // Hiển thị ảnh mặc định
        }



        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(restaurant);
            }
        });
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

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView; // TextView mới để hiển thị địa chỉ
        ImageView imageView;
        TextView ratingTextView;
//        RatingBar ratingBar;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurantName);
            addressTextView = itemView.findViewById(R.id.restaurantAddress); // Kết nối với TextView địa chỉ
            imageView = itemView.findViewById(R.id.restaurantImage);
            ratingTextView = itemView.findViewById(R.id.restaurantRating);
        }
    }

    public void updateData(List<Restaurant> newRestaurantList) {
        this.restaurantList.clear();
        this.restaurantList.addAll(newRestaurantList);
        notifyDataSetChanged();
    }

}
