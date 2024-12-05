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

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Set dữ liệu cho các view
        holder.userName.setText(user.getName() != null ? user.getName() : user.getTaiKhoan());
        holder.userPhone.setText(user.getSDT());
        holder.userRole.setText(user.isRole() ? "Admin" : "User");

        // Hiển thị ảnh đại diện
        Glide.with(context)
                .load(user.getImage()) // Đường dẫn hoặc URL
                .placeholder(R.drawable.person_icon) // Hình mặc định
                .error(R.drawable.person_icon) // Hình lỗi
                .into(holder.userAvatar);

        // Gắn tag để truyền dữ liệu vào sự kiện nhấn
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
}
