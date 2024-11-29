package com.example.pandora.Slider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pandora.Class.User;
import com.example.pandora.Home;
import com.example.pandora.LocationFragment;
import com.example.pandora.Profile;
import com.example.pandora.Setting;

public class SliderPagerAdapter extends FragmentStateAdapter {
    private final boolean isLogin;
    private final String userName;
    int userid;
    User user;
    public SliderPagerAdapter(@NonNull FragmentActivity fragmentActivity, boolean isLogin, String userName, int userid, User user) {
        super(fragmentActivity);
        this.isLogin = isLogin;
        this.userName = userName;
        this.userid= userid;
        this.user = user;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new Home();
                break;
            case 1:
                fragment = new Profile();
                break;
            case 2:
                fragment = new Setting();
                break;
            case 3:
                fragment = new LocationFragment();
                break;
            default:
                fragment = new Home();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isLogin", isLogin);
        bundle.putString("userName", userName);
        bundle.putInt("userid", userid);
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
