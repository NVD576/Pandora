package com.example.pandora.AdminProperties;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

import java.util.List;

public class AccountProperties extends AppCompatActivity {

    User user;
    private boolean isAdmin = true;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<User> userList;
        UserDatabase db = new UserDatabase(getApplicationContext());
        db.open();
        userList = db.getAllUsers();
        db.close();

        UserAdapter adapter = new UserAdapter(this, userList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                showEditAccountDialog(user);
            }
        });

    }

    private void showEditAccountDialog(User user) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_account, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Đặt sự kiện cho nút đóng
        ImageView btnClose = dialogView.findViewById(R.id.close_button);
        btnClose.setOnClickListener(v -> {
            // Lưu lại giá trị role đã chọn trước khi đóng
            user.setRole(isAdmin); // currentRole lưu trạng thái role đã chọn
            UserDatabase db = new UserDatabase(getApplicationContext());
            db.open();
            db.updateUser(user);
            db.close();
            alertDialog.dismiss();
            recreate();
        });

        // Thêm logic chuyển đổi toggle
        View toggleSlider = dialogView.findViewById(R.id.toggle_slider);
        TextView adminLabel = dialogView.findViewById(R.id.admin_label);
        TextView userLabel = dialogView.findViewById(R.id.user_label);

        // Thiết lập giá trị role hiện tại và vị trí toggle
        boolean currentRole = user.isRole(); // Biến lưu trạng thái hiện tại
        toggleSlider.post(() -> { // Đảm bảo slider đã tính toán kích thước
            float initialPosition = currentRole ? 0 : toggleSlider.getWidth() + 20;
            toggleSlider.setTranslationX(initialPosition);
        });

        adminLabel.setOnClickListener(v -> {
            // Chuyển toggle về Admin
            animateSlider(toggleSlider, 0);
            isAdmin = true;
            user.setRole(isAdmin);
        });

        userLabel.setOnClickListener(v -> {
            // Chuyển toggle về User
            animateSlider(toggleSlider, 1);
            isAdmin = false;
            user.setRole(isAdmin);

        });

        // Đặt sự kiện cho nút Xóa tài khoản
        TextView deleteAccount = dialogView.findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(v -> {
            // Xóa tài khoản khỏi cơ sở dữ liệu
            UserDatabase db = new UserDatabase(getApplicationContext());
            db.open();
            db.deleteUser(user.getId());
            recyclerView.getAdapter().notifyDataSetChanged();
            alertDialog.dismiss();
            db.close();
            recreate();
        });

        alertDialog.show();
    }




    private void animateSlider(View slider, int position) {
        float targetX = position == 0 ? 0 : slider.getWidth() + 20; // Vị trí cần di chuyển
        ObjectAnimator animator = ObjectAnimator.ofFloat(slider, "translationX", targetX);
        animator.setDuration(300); // Thời gian di chuyển
        animator.start();
    }
}