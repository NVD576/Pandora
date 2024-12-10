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

import com.example.pandora.Class.Restaurant;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.Comparator;
import java.util.List;

public class RestaurantHighRatingAdapter extends RecyclerView.Adapter<RestaurantHighRatingAdapter.RestaurantViewHolder> {

    private List<Restaurant> highRatingRestaurantList;
    private OnItemClickListener listener;
    private Context context;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RestaurantHighRatingAdapter(List<Restaurant> highRatingRestaurants) {
        this.highRatingRestaurantList = highRatingRestaurants;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant restaurant = highRatingRestaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getAddress());
        holder.ratingTextView.setText(String.valueOf(restaurant.getStar()));

        // Load image từ bộ nhớ trong
        Bitmap bitmap = loadImageFromInternalStorage(restaurant.getImage());
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
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
        return highRatingRestaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView; // TextView mới để hiển thị địa chỉ
        ImageView imageView;
        TextView ratingTextView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurantName);
            addressTextView = itemView.findViewById(R.id.restaurantAddress); // Kết nối với TextView địa chỉ
            imageView = itemView.findViewById(R.id.restaurantImage);
            ratingTextView = itemView.findViewById(R.id.restaurantRating);
        }
    }

    // Cập nhật dữ liệu và sắp xếp danh sách theo đánh giá từ cao xuống thấp
    public void updateData(List<Restaurant> newHighRatingRestaurantList) {
        this.highRatingRestaurantList.clear(); // Xóa dữ liệu cũ
        this.highRatingRestaurantList.addAll(newHighRatingRestaurantList); // Thêm dữ liệu mới

        // Sắp xếp danh sách nhà hàng theo đánh giá từ cao xuống thấp
        this.highRatingRestaurantList.sort(new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                // So sánh đánh giá sao của nhà hàng
                return Float.compare(r2.getStar(), r1.getStar()); // Đánh giá cao hơn sẽ ở trên
            }
        });

        notifyDataSetChanged(); // Thông báo dữ liệu thay đổi
    }
}
