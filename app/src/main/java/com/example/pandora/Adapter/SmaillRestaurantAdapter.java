package com.example.pandora.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Restaurant;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.List;

public class SmaillRestaurantAdapter extends RecyclerView.Adapter<SmaillRestaurantAdapter.SmaillRestaurantViewHolder> {

    private Context context;
    private static List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    public SmaillRestaurantAdapter(Context context,List<Restaurant> restaurantList) {
        this.context= context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public SmaillRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new SmaillRestaurantViewHolder(view,listener);
    }



    @Override
    public void onBindViewHolder(@NonNull SmaillRestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Set dữ liệu cho các view
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getAddress());
        holder.restaurantRating.setText(String.valueOf(restaurant.getStar()));


        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(restaurant.getImage());
            holder.restaurantImage.setImageBitmap(bitmap);
        } else {
            holder.restaurantImage.setImageResource(R.drawable.pandora_background); // Đặt ảnh mặc định nếu không tải được ảnh
        }
        // Gắn tag để truyền dữ liệu vào sự kiện nhấn
        holder.itemView.setTag(restaurant);
    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class SmaillRestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName, restaurantAddress, restaurantRating;
        ImageView restaurantImage;

        public SmaillRestaurantViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantAddress = itemView.findViewById(R.id.restaurantAddress);
            restaurantImage = itemView.findViewById(R.id.restaurantImage);
            restaurantRating = itemView.findViewById(R.id.restaurantRating);

            // Thiết lập sự kiện nhấn vào item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(restaurantList.get(position));  // Truyền đối tượng Restaurant
                    }
                }
            });
        }
    }



    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant); // Sửa thành Restaurant
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
