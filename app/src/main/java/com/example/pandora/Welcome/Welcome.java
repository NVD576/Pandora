package com.example.pandora.Welcome;

import android.content.Intent;
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

        if(db.isUserTableEmpty()){
            User admin= new User("duc", "123123","1234567890", true);

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
}