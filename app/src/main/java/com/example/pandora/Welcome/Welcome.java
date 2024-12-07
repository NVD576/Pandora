package com.example.pandora.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Welcome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView loadingText = findViewById(R.id.loading);
        loadingText.setAlpha(0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingText.setAlpha(1f);
            }
        },500);
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        loadingText.startAnimation(blinkAnimation);

        UserDatabase db = new UserDatabase(Welcome.this); // Khởi tạo UserDatabase
        db.open(); // Mở kết nối cơ sở dữ liệu
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(db.isUserTableEmpty())
            editor.putBoolean("isLogin", false);
        editor.apply();
        if(db.isUserTableEmpty()){
            String pass= "123123";
            User admin= new User("duc", hash(pass),"1234567890", true);
            admin.setName("Admin");
            admin.setRoleUser(true);
            admin.setRoleCategory(true);
            admin.setRoleRestaurant(true);
            admin.setRoleReview(true);

            db.addUser(admin, getApplicationContext());
        }
        db.close();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent myIntent = new Intent(Welcome.this, Lobby.class);
                startActivity(myIntent);
                finish();
            }
        },2000);
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