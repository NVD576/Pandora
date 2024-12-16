package com.example.pandora.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.R;

import java.util.Locale;

public class Setting extends Fragment {

    private boolean isLogin = false;
    private int userid;
    private SharedPreferences sharedPreferences;
    TextView deleteAccount;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 is default if not found
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        TextView edtPolicy = view.findViewById(R.id.policy);
        edtPolicy.setOnClickListener(v -> showEditPolicyDialog());

        TextView edtLanguage = view.findViewById(R.id.language);
        edtLanguage.setOnClickListener(v -> showEditLanguageDialog());

        deleteAccount = view.findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(v -> showEditDeleteAccountDialog());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật giá trị isLogin từ SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        // Cập nhật giao diện người dùng dựa trên giá trị isLogin
        deleteAccount = requireView().findViewById(R.id.deleteAccount);
        if (!isLogin) {
            deleteAccount.setAlpha(0f);
            deleteAccount.setEnabled(false);
            deleteAccount.setVisibility(View.GONE);
        } else {
            deleteAccount.setAlpha(1f);
            deleteAccount.setEnabled(true);
        }
    }
    private void showEditPolicyDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_policy_setting, null);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void showEditLanguageDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_language_setting, null);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        RadioButton radioVietnamese = dialogView.findViewById(R.id.radioVietnamese);
        RadioButton radioEnglish = dialogView.findViewById(R.id.radioEnglish);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        // Lấy ngôn ngữ hiện tại từ SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String currentLang = preferences.getString("My_Lang", "vi");
        if ("vi".equals(currentLang)) {
            radioVietnamese.setChecked(true);
        } else if ("en".equals(currentLang)) {
            radioEnglish.setChecked(true);
        }

        btnOk.setOnClickListener(v -> {
            String selectedLang = radioVietnamese.isChecked() ? "vi" : "en";
            setLocale(selectedLang); // Thay đổi ngôn ngữ
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources resources = requireContext().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Lưu ngôn ngữ vào SharedPreferences
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Khởi động lại Activity để áp dụng ngôn ngữ mới
        requireActivity().recreate();
    }


    private void showEditDeleteAccountDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_account_setting, null);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CheckBox check = dialogView.findViewById(R.id.checkDelete);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        check.setChecked(false);
        // Thêm listener cho CheckBox
        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Kích hoạt hoặc vô hiệu hóa nút Save dựa trên trạng thái của CheckBox
            btnSave.setEnabled(isChecked);
        });

        // Thiết lập listener cho nút Save
        btnSave.setOnClickListener(v -> {
//            if (check.isChecked()) {
//                deleteAccount();
//            } else {
//                alertDialog.dismiss();
//            }
            deleteAccount();
        });


        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void deleteAccount() {
        if (userid == -1) {
            Toast.makeText(getContext(), "User not found, cannot delete", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLogin)
        {
            UserDatabase db = new UserDatabase(getContext());
            db.open();
            db.deleteUser(userid);
            db.close();

            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLogin", false);
            editor.remove("userid");
            editor.apply();

            Intent myIntent = new Intent(getActivity(), Login.class);
            startActivity(myIntent);
        }
    }
}
