package com.example.pandora.Main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.pandora.Home;
import com.example.pandora.Profile;
import com.example.pandora.R;
import com.example.pandora.Setting;
import com.example.pandora.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class Lobby extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    NavigationView navigationView;

    Toolbar toolbar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.Open, R.string.Close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.lavender_dark));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new Home()).commit();
            navigationView.setCheckedItem(R.id.Home);
        }


    }

//    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.Home){
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new Home()).commit();
        } else  if (item.getItemId()==R.id.Profile){
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new Profile()).commit();
        } else  if (item.getItemId()==R.id.Setting){
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new Setting()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}