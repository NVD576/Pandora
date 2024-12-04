package com.example.pandora.AdminProperties;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thư viện để tải ảnh
import com.example.pandora.Class.User;
import com.example.pandora.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Set dữ liệu cho các view
        holder.userName.setText(user.getName() != null ? user.getName() : "No Name");
        holder.userPhone.setText(user.getSDT());
        // Hiển thị ảnh đại diện
        Glide.with(context)
                .load(user.getImage()) // Đường dẫn hoặc URL
                .placeholder(R.drawable.person_icon) // Hình mặc định
                .error(R.drawable.person_icon) // Hình lỗi
                .into(holder.userAvatar);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userPhone;
        ImageView userAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPhone = itemView.findViewById(R.id.user_phone);
            userAvatar = itemView.findViewById(R.id.user_avatar);
        }
    }
}
