package com.example.pandora;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;

public class Profile extends Fragment {


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean isLogin = false;
        String userName = "";

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView login = view.findViewById(R.id.loginProfile);

        Bundle bundle = getArguments();
        if (bundle!=null){
            isLogin = bundle.getBoolean("isLogin", false);
            if (isLogin){
                userName = bundle.getString("userName");
            }
        }
        if (isLogin)
        {
            login.setText(userName);
            Drawable[] drawables = login.getCompoundDrawablesRelative();
            login.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawables[0], // drawableStart
                    drawables[1], // drawableTop
                    null,         // drawableEnd
                    drawables[3]  // drawableBottom
            );
        }
        else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getActivity(), Login.class);
                    startActivity(myIntent);
                }
            });
        }

        return view;
    }
}