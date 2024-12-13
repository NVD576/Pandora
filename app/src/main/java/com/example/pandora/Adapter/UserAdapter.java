package com.example.pandora.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.User;
import com.example.pandora.R;

import java.io.FileInputStream;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnItemClickListener listener;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view,listener);
    }

    public void setFilteredList(List<User> filteredList){
        this.userList = filteredList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Set dữ liệu cho các view
        holder.userName.setText(user.getName() != null ? user.getName() : user.getUserName());
        holder.userPhone.setText(user.getNumberPhone());
        holder.userRole.setText(user.isRole()>0 ? "Admin" : "User");
        Log.d("Image Path", "Image path: " + user.getImage());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            Bitmap bitmap = loadImageFromInternalStorage(user.getImage());
            holder.userAvatar.setImageBitmap(bitmap);
        } else {
            holder.userAvatar.setImageResource(R.drawable.person_icon); // Đặt ảnh mặc định nếu không tải được ảnh
        }
        holder.itemView.setTag(user);
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userPhone, userRole;
        ImageView userAvatar;


        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPhone = itemView.findViewById(R.id.user_phone);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userRole = itemView.findViewById(R.id.user_role);


            // Thiết lập sự kiện nhấn vào item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        listener.onItemClick((User) v.getTag());
                    }
                }
            });
        }


    }


    public interface OnItemClickListener {
        void onItemClick(User user);
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
