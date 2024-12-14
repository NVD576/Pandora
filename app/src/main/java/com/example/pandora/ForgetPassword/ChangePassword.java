package com.example.pandora.ForgetPassword;

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
import com.example.pandora.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePassword extends AppCompatActivity {
    Button btnXacNhan;
    EditText edMK, edMK2;
    int userid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        userid= intent.getIntExtra("userid", -1);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnXacNhan = findViewById(R.id.btnXacNhan);
        edMK= findViewById(R.id.edMK);
        edMK2= findViewById(R.id.edMK2);


        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edMK.getText().toString().equals(edMK2.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Đổi mật khẩu thành công",Toast.LENGTH_SHORT).show();
                    String passUpdate = edMK.getText().toString();
                    passUpdate=hash(passUpdate);
                    updatePassword(passUpdate);
                    Intent intent= new Intent(ChangePassword.this, Login.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Mật khẩu không trùng khớp!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updatePassword(String passUpdate){
        UserDatabase db = new UserDatabase(getApplicationContext());
        db.open();
        User u=db.getUserById(userid);
        u.setPassword(passUpdate);
        db.updateUser(u);
        db.close();
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