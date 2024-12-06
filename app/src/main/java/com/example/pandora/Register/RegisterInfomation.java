package com.example.pandora.Register;

import android.annotation.SuppressLint;
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

import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;
import com.example.pandora.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterInfomation extends AppCompatActivity {

    SignInButton btnGoogle;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String name;
    String email;
    @SuppressLint("MissingInflatedId")
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        btnGoogle= findViewById(R.id.btnGoogleSignIn);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });




        //xac nhan lay code
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
               numberPhone= hash(numberPhone);
               User newUser = new User(userName, password, numberPhone);
               UserDatabase db = new UserDatabase(RegisterInfomation.this);

               db.open();


               if(db.addUser(newUser, RegisterInfomation.this)){
                   Toast.makeText(getApplicationContext(),"Đăng kí thành công",Toast.LENGTH_SHORT).show();
                   Intent myIntent = new Intent(RegisterInfomation.this, Login.class);
                   startActivity(myIntent);
               }

               // Đóng kết nối
               db.close();


           }
       });

    }
    void signIn(){
        Intent signInIntent= gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode==1000){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                task.getResult(ApiException.class);

                navigateToSecondActivity();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

        }

    }
    void navigateToSecondActivity(){
        Intent  intent= new Intent(RegisterInfomation.this, Lobby.class);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount((this));
        if(acct!=null){
            name= acct.getDisplayName();
            email = acct.getDisplayName();
        }
        UserDatabase db = new UserDatabase(this);
        User nUser= db.getUserByTaiKhoan(email);
        if (nUser == null) {
            nUser= new User(email,name);
            db.addUser(nUser, this);
        }
        db.close();
        startActivity(intent);
    }
    // Hàm  băm mật khẩu
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