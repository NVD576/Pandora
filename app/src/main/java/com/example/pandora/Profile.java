package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;

import java.util.Objects;

public class Profile extends Fragment {


    boolean isLogin = false;
    String userName = "";

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Intent myIntent = new Intent(getActivity(), Login.class);
                    startActivity(myIntent);
                }
            }
        });

        TextView changeInfo = view.findViewById(R.id.changeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Toast.makeText(getActivity(),"Yêu cầu đăng nhập",Toast.LENGTH_SHORT).show();
                }
                else {
                    showEditProfileDialog();
                }
            }
        });

        TextView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Toast.makeText(getActivity(),"Yêu cầu đăng nhập",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    login.setText("Đăng nhập");
                    Drawable drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_forward_ios_24);
                    Drawable[] drawables = login.getCompoundDrawablesRelative();
                    login.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            drawables[0],
                            drawables[1],
                            drawableEnd,
                            drawables[3]
                    );
                    isLogin = false;
                }
            }
        });

        return view;
    }

    private void showEditProfileDialog() {
        // Tạo view cho hộp thoại
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // Ánh xạ các EditText trong hộp thoại
        EditText edtNumberPhone = dialogView.findViewById(R.id.edtNumberPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        // Tạo hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa thông tin cá nhân")
                .setView(dialogView)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Lấy dữ liệu từ các EditText
                        String newNumberPhone = edtNumberPhone.getText().toString();
                        String newEmail = edtEmail.getText().toString();

                        // Cập nhật thông tin (Ví dụ: bạn có thể lưu vào cơ sở dữ liệu)
                        updateProfile(newNumberPhone, newEmail);
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void updateProfile(String numberPhone, String email) {
        // Cập nhật UI hoặc dữ liệu (ví dụ, cập nhật TextView hoặc lưu vào cơ sở dữ liệu)
    }
}