package com.example.pandora.Main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import com.example.pandora.Home;
import com.example.pandora.Profile;
import com.example.pandora.R;
import com.example.pandora.Setting;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Lobby extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ImageView btnSave = findViewById(R.id.save);
        btnSave.setOnClickListener(view -> showLoginAlertDialog());

        loadFragment(new Home());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    loadFragment(new Home());
                    return true;
                }
                if (item.getItemId() == R.id.profile) {
                    loadFragment(new Profile());
                    return true;
                }
                if (item.getItemId() == R.id.setting) {
                    loadFragment(new Setting());
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void showLoginAlertDialog() {
        new AlertDialog.Builder(Lobby.this)
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này")
                .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}
