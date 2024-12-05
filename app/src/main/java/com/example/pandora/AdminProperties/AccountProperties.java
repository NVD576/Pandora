package com.example.pandora.AdminProperties;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.UserAdapter;
import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.R;

import java.util.List;

public class AccountProperties extends AppCompatActivity {

    User u;
    private boolean isAdmin = true;
    RecyclerView recyclerView;
    LinearLayout listCheckBox;
    TextView deleteAccount,titleAlert;
    RelativeLayout toggle_container;
    int userid;
    UserDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // false là giá trị mặc định
        db= new UserDatabase(getApplicationContext());
        db.open();
        u= db.getUserById(userid);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<User> userList;
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
        listCheckBox = dialogView.findViewById(R.id.cbPhanQuyen);
        deleteAccount= dialogView.findViewById(R.id.deleteAccount);
        toggle_container = dialogView.findViewById(R.id.toggle_container);
        titleAlert =dialogView.findViewById(R.id.titleAlert);
        isAdmin=user.isRole();

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        if(userid==1){
            if(user.getId()==1){
                deleteAccount.setVisibility(View.GONE);
                for (int i = 0; i < listCheckBox.getChildCount(); i++) {
                    View child = listCheckBox.getChildAt(i);
                    if (child instanceof CheckBox) { // Kiểm tra nếu là CheckBox
                        child.setEnabled(false); // Vô hiệu hóa CheckBox
                    }
                }
                toggle_container.setVisibility(View.GONE);
                titleAlert.setText("Không thể chỉnh sửa");
            }else {
                deleteAccount.setVisibility(View.VISIBLE);
                for (int i = 0; i < listCheckBox.getChildCount(); i++) {
                    View child = listCheckBox.getChildAt(i);
                    if (child instanceof CheckBox) { // Kiểm tra nếu là CheckBox
                        child.setEnabled(true); // Vô hiệu hóa CheckBox
                    }
                }
                toggle_container.setVisibility(View.VISIBLE);
                titleAlert.setText("Chỉnh sửa tài khoản");
            }
        }else {
            if (isAdmin ) {
                // Người dùng có role == true (Admin), không thể xóa hoặc sửa quyền của chính mình
                deleteAccount.setVisibility(View.GONE); // Không thể xóa
                for (int i = 0; i < listCheckBox.getChildCount(); i++) {
                    View child = listCheckBox.getChildAt(i);
                    if (child instanceof CheckBox) {
                        child.setEnabled(false); // Không thể chỉnh sửa quyền
                    }
                }
                toggle_container.setVisibility(View.GONE);
                titleAlert.setText("Bạn không thể thay đổi quyền");
            } else {
                // Người dùng ko có role == true (Admin), không thể xóa hoặc sửa quyền của chính mình
                deleteAccount.setVisibility(View.VISIBLE); // có thể xóa
                listCheckBox.setVisibility(View.GONE);
                toggle_container.setVisibility(View.VISIBLE);
                titleAlert.setText("Chỉnh sửa tài khoản");
            }
        }

//        else{
//            listCheckBox.setEnabled(true);
//        }



//        if (isAdmin) {
//            deleteAccount.setVisibility(View.VISIBLE);
//        } else {
//
//        }
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
            listCheckBox.setVisibility(View.VISIBLE);


        });

        userLabel.setOnClickListener(v -> {
            // Chuyển toggle về User
            animateSlider(toggleSlider, 1);
            isAdmin = false;
            user.setRole(isAdmin);
            listCheckBox.setVisibility(View.GONE);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo đóng database khi Activity bị hủy
        if (db != null) {
            db.close();
        }
    }
}