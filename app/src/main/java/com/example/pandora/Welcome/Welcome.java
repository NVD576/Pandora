package com.example.pandora.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.Class.Location;
import com.example.pandora.Class.User;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;
import com.example.pandora.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Welcome extends AppCompatActivity {
    String DB_PATH_SUFFIX = "/databases/";
    String DATABASE_NAME = "reviewFood.db";
     private static final String IMAGE_PATH_SUFFIX = "/files/";

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
        //nạp csdl
        processCopy();

        TextView loadingText = findViewById(R.id.loading);
        loadingText.setAlpha(0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingText.setAlpha(1f);
            }
        }, 500);
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        loadingText.startAnimation(blinkAnimation);

        UserDatabase db = new UserDatabase(Welcome.this); // Khởi tạo UserDatabase
        db.open(); // Mở kết nối cơ sở dữ liệu
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (db.isUserTableEmpty())
            editor.putBoolean("isLogin", false);
        editor.apply();
        if (db.isUserTableEmpty()) {
            String pass = "123123";
            User admin = new User("admin", hash(pass), "1234567890", 1);
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
        }, 2000);
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

    private void processCopy() {
    //private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
    // TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
    // Path to the just created empty db
            String outFileName = getDatabasePath();
    // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
    // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
    // transfer bytes from the inputfile to the outputfile
    // Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
    // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
    // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}