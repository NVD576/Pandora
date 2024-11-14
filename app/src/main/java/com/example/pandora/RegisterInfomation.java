package com.example.pandora;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterInfomation extends AppCompatActivity {
    UserDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_infomation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText edUserName = findViewById(R.id.addUser);
        EditText edPassword = findViewById(R.id.addPassword);
        EditText edRePassword = findViewById(R.id.reAddPassword);
        EditText edNumberPhone = findViewById(R.id.addNumberPhone);


       Button btnAcp = findViewById(R.id.btnAcp);
       btnAcp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String userName = edUserName.getText().toString();
               String password = edPassword.getText().toString();
               String rePassword = edRePassword.getText().toString();
               String numberPhone = edNumberPhone.getText().toString();
               if (userName.length()==0 || password.length()==0 || rePassword.length()==0 || numberPhone.length()==0){
                   Toast.makeText(getApplicationContext(),"Yêu cầu nhập đủ thông tin",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (!password.equals(rePassword)){
                   Toast.makeText(getApplicationContext(),"Mật khẩu không trùng khớp",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (numberPhone.length()<10){
                   Toast.makeText(getApplicationContext(),"Số điện thoại không hợp lệ",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (password.length()<6){
                   Toast.makeText(getApplicationContext(),"Mật khẩu phải từ 6 kí tự trở lên",Toast.LENGTH_SHORT).show();
                   return;
               }
               User newUser = new User(userName, password, numberPhone);

               // Mở kết nối với cơ sở dữ liệu
               db = new UserDatabase( RegisterInfomation.this);
               db.open();

               // Thêm người dùng vào cơ sở dữ liệu
               if(db.addUser(newUser, RegisterInfomation.this)){
                   Intent myIntent = new Intent(RegisterInfomation.this, Login.class);
                   startActivity(myIntent);
               }

               // Đóng kết nối
               db.close();


           }
       });
    }
}