package com.example.pandora.AdminProperties;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    CheckBox cbQlUser, cbQlCategory, cbQlRestaurant, cbQlReview;

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


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAccountAlertDialog();
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        EditText search = findViewById(R.id.search_input);


    }

    @SuppressLint("MissingInflatedId")
    private void showEditAccountDialog(User user) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_account, null);
        listCheckBox = dialogView.findViewById(R.id.cbPhanQuyen);
        deleteAccount= dialogView.findViewById(R.id.deleteAccount);
        toggle_container = dialogView.findViewById(R.id.toggle_container);
        titleAlert =dialogView.findViewById(R.id.titleAlert);
        isAdmin=user.isRole();

        cbQlUser= dialogView.findViewById(R.id.cbQlUser);
        cbQlCategory= dialogView.findViewById(R.id.cbQlCategory);
        cbQlRestaurant= dialogView.findViewById(R.id.cbQlRestaurant);
        cbQlReview= dialogView.findViewById(R.id.cbQlReview);

// Thiết lập trạng thái ban đầu của các CheckBox
        cbQlUser.setChecked(user.isRoleUser());
        cbQlCategory.setChecked(user.isRoleCategory());
        cbQlRestaurant.setChecked(user.isRoleRestaurant());
        cbQlReview.setChecked(user.isRoleReview());

// Lắng nghe sự kiện thay đổi trạng thái của CheckBox
        cbQlUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setRoleUser(isChecked);
        });

        cbQlCategory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setRoleCategory(isChecked);
        });

        cbQlRestaurant.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setRoleRestaurant(isChecked);
        });

        cbQlReview.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setRoleReview(isChecked);
        });


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
                if(isAdmin){
                    for (int i = 0; i < listCheckBox.getChildCount(); i++) {
                        View child = listCheckBox.getChildAt(i);
                        if (child instanceof CheckBox) { // Kiểm tra nếu là CheckBox
                            child.setEnabled(true);
                        }
                    }
                }
                else {
                    listCheckBox.setVisibility(View.GONE);
                }

                toggle_container.setVisibility(View.VISIBLE);
                titleAlert.setText("Chỉnh sửa tài khoản");
            }
        }else {
            if (isAdmin ) {
                // Người dùng có role == true (Admin), không thể xóa hoặc sửa quyền
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
            recyclerView.getAdapter().notifyDataSetChanged();

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
            user.setRoleUser(false);
            user.setRoleRestaurant(false);
            user.setRoleCategory(false);
            user.setRoleReview(false);
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

    private void showAddAccountAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        View dialogView = inflater.inflate(R.layout.dialog_add_account_admin, null);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Tìm các thành phần trong layout dialog
        EditText edUserName = dialogView.findViewById(R.id.addUser);
        EditText edPassword = dialogView.findViewById(R.id.addPassword);
        EditText edRePassword = dialogView.findViewById(R.id.reAddPassword);
        EditText edNumberPhone = dialogView.findViewById(R.id.addNumberPhone);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);

        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            String userName = edUserName.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String rePassword = edRePassword.getText().toString().trim();
            String numberPhone = edNumberPhone.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (userName.isEmpty() || password.isEmpty() || rePassword.isEmpty() || numberPhone.isEmpty()) {
                Toast.makeText(this, "Yêu cầu nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(rePassword)) {
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            if (numberPhone.length() < 10) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải từ 6 kí tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mã hóa mật khẩu
            String hashedPassword = hash(password);

            // Thêm người dùng vào cơ sở dữ liệu
            User newUser = new User(userName, hashedPassword, numberPhone);
            UserDatabase db = new UserDatabase(this);
            db.open();
            if (db.addUser(newUser, this)) {
                Toast.makeText(this, "Thêm tài khoản thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
            }
            db.close();

            // Đóng dialog sau khi lưu
            recreate();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}