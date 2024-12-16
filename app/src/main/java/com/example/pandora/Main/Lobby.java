package com.example.pandora.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pandora.Class.User;
import com.example.pandora.R;
import com.example.pandora.Adapter.SliderPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Lobby extends BaseActivity {

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
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định
        Intent intent = getIntent();
        if (isLogin){
            user = (User) getIntent().getSerializableExtra("user");
            userName = intent.getStringExtra("userName");
            userid= intent.getIntExtra("userid", -1);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewPager2);
        SliderPagerAdapter adapter = new SliderPagerAdapter(this, isLogin, userName, userid, user);
        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);
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

    }

}
