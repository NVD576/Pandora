package com.example.pandora.Main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pandora.Class.User;
import com.example.pandora.Home;
import com.example.pandora.Profile;
import com.example.pandora.R;
import com.example.pandora.SaveLocationReview;
import com.example.pandora.Setting;
import com.example.pandora.Slider.SliderPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Lobby extends AppCompatActivity {

    ViewPager2 viewPager2;
    String userName;
    int userid;
    Boolean isLogin = false;
    User user;
    BottomNavigationView bottomNavigationView;
    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Intent intent = getIntent();
        isLogin = intent.getBooleanExtra("isLogin", false);
        if (isLogin){
            user = (User) getIntent().getSerializableExtra("user");
            userName = intent.getStringExtra("userName");
            userid= intent.getIntExtra("userid", -1);
        }
        ImageView btnSave = findViewById(R.id.save);
        btnSave.setOnClickListener(view -> showLoginAlertDialog());

//        loadFragment(new Home(), isLogin, userName);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewPager2);
        SliderPagerAdapter adapter = new SliderPagerAdapter(this, isLogin, userName, userid, user);
        viewPager2.setAdapter(adapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    viewPager2.setCurrentItem(0);
                    return true;
                }
                if (item.getItemId() == R.id.profile) {
                    viewPager2.setCurrentItem(1);
                    return true;
                }
                if (item.getItemId() == R.id.setting) {
                    viewPager2.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.profile);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.setting);
                        break;
                }
            }
        });

        if (isLogin){
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(Lobby.this, SaveLocationReview.class);
                    startActivity(myIntent);
                }
            });
        }
        else{
            btnSave.setOnClickListener(view -> showLoginAlertDialog());
        }
    }

//    private void loadFragment(Fragment fragment, Boolean isLogin, String userName) {
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isLogin", isLogin);
//        bundle.putString("userName", userName);
//        fragment.setArguments(bundle);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
//        fragmentTransaction.commit();
//
//    }

    private void showLoginAlertDialog() {
        new AlertDialog.Builder(Lobby.this)
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này")
                .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}
