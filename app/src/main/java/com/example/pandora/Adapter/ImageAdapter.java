package com.example.pandora.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Image;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.ImageDatabase;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Restaurant> imageList;
    private Context context;
    private OnItemClickListener listener;

    // Constructor để nhận vào danh sách ID hình ảnh
    public ImageAdapter(Context context,List<Restaurant> imageList) {
        this.imageList = imageList;
        this.context= context;
    }

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }
    public void setOnItemClickListener(ImageAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item cho từng hình ảnh
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Đặt ID hình ảnh tại vị trí hiện tại
        Restaurant restaurant = imageList.get(position);

        ImageDatabase imageDatabase= new ImageDatabase(context);
        imageDatabase.open();
        List<Image> image= imageDatabase.getImageByRestaurantId(restaurant.getId());
        imageDatabase.close();

        if (!image.isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(image.get(0).getImageUrl());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.pandora_background); // Hiển thị ảnh mặc định
        }
        // Gán sự kiện click
        holder.imageView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(imageList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ViewHolder class để giữ các view cho từng item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
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
