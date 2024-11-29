package com.example.pandora.Slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pandora.Home;
import com.example.pandora.LocationFragment;

public class SliderHomeAdapter extends FragmentStateAdapter {

    public SliderHomeAdapter(@NonNull Home fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new LocationFragment();
                break;
            default:
                fragment=new LocationFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
