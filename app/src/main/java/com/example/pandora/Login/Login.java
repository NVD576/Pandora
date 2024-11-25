package com.example.pandora.Login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.ForgetPassword.ForgetPassword;
import com.example.pandora.Main.Lobby;
import com.example.pandora.R;
import com.example.pandora.Register.Register;
import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;

public class Login extends AppCompatActivity {

    EditText edUserName ;
    EditText password;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView img = findViewById(R.id.imageView);
        TextView titleLogin = findViewById(R.id.titleLogin);
        ObjectAnimator animatorImage = ObjectAnimator.ofFloat(img,"translationY",0f,-600f);
        ObjectAnimator scaleXImage = ObjectAnimator.ofFloat(img,"scaleX",1f,0.8f);
        ObjectAnimator scaleYImage = ObjectAnimator.ofFloat(img,"scaleY",1f,0.8f);
        ObjectAnimator animatorTitle = ObjectAnimator.ofFloat(titleLogin,"translationY",0f,-10f);
        ObjectAnimator scaleXTitle = ObjectAnimator.ofFloat(titleLogin,"scaleX",0.5f,1.5f);
        ObjectAnimator scaleYTitle = ObjectAnimator.ofFloat(titleLogin,"scaleY",0.5f,1.5f);
        AnimatorSet animationImage = new AnimatorSet();
        animationImage.playTogether(animatorImage,scaleXImage,scaleYImage,animatorTitle,scaleXTitle,scaleYTitle);
        animationImage.setStartDelay(1000);
        animationImage.setDuration(750);
        animationImage.start();
        LinearLayout layoutLogin = findViewById(R.id.loginInterface);
        layoutLogin.setAlpha(0f);
        layoutLogin.setVisibility(ImageView.VISIBLE);
        ObjectAnimator loginInterface = ObjectAnimator.ofFloat(layoutLogin, "alpha",0f,1f);
        LinearLayout layoutButton = findViewById(R.id.layoutButton);
        layoutButton.setAlpha(0f);
        layoutButton.setVisibility(ImageView.VISIBLE);
        ObjectAnimator buttonInterface = ObjectAnimator.ofFloat(layoutButton,"alpha",0f,1f);
        AnimatorSet animationLinerLayout = new AnimatorSet();
        animationLinerLayout.playTogether(loginInterface,buttonInterface);
        animationLinerLayout.setStartDelay(1000);
        animationLinerLayout.setDuration(500);
        animationLinerLayout.start();
        CheckBox checkBox = findViewById(R.id.password_show);
        edUserName = findViewById(R.id.edTK);
        password = findViewById(R.id.edPW);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox.isChecked())
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                password.setSelection(password.getText().length());
            }
        });
        TextView forgetText = findViewById(R.id.forget);
        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Login.this, ForgetPassword.class);
                startActivity(myIntent);
            }
        });
        TextView txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Login.this, Register.class);
                startActivity(myIntent);
            }
        });
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edUserName.getText().toString();
                String pw = password.getText().toString();
                if (userName.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra đăng nhập
                UserDatabase db = new UserDatabase(Login.this); // Khởi tạo UserDatabase
                db.open(); // Mở kết nối cơ sở dữ liệu

                if(db.isUserTableEmpty()){
                    User admin= new User("duc", "123123","1234567890", true);
                    db.addUser(admin, getApplicationContext());
                }

                // Tìm người dùng theo tài khoản và mật khẩu
                User user = db.getUserByUsernameAndPassword(userName, pw);

                db.close(); // Đóng kết nối cơ sở dữ liệu

                if (user != null) {
                    // Đăng nhập thành công, chuyển sang màn hình tiếp theo
                    Intent myIntent = new Intent(Login.this, Lobby.class);
                    myIntent.putExtra("isLogin", true);
//                    Log.e("Login", "UserID before " +user.getId());
                    myIntent.putExtra("userid", user.getId());
//                    Log.e("Login", "UserID aft " +user.getId());
                    myIntent.putExtra("userName", user.getTaiKhoan());
                    startActivity(myIntent);
                     // Đảm bảo không quay lại màn hình đăng nhập
                } else {
                    // Đăng nhập thất bại
                    Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
