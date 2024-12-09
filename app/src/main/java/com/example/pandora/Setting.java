package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;

public class Setting extends Fragment {

    private boolean isLogin = false;
    private int userid;
    private SharedPreferences sharedPreferences;

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

        TextView deleteAccount = view.findViewById(R.id.deleteAccount);

        if (!isLogin)
        {
            deleteAccount.setAlpha(0f);
            deleteAccount.setEnabled(false);
        }
        else {
            deleteAccount.setAlpha(1f);
            deleteAccount.setEnabled(true);
        }

        deleteAccount.setOnClickListener(v -> showEditDeleteAccountDialog());

        return view;
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

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
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
        if (!check.isChecked())
        {
            btnSave.setEnabled(false);
        }
        else {
            btnSave.setEnabled(true);
        }
        btnSave.setOnClickListener(v -> {

            if (check.isChecked()) {
                deleteAccount();
            } else {
                alertDialog.dismiss();
            }
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
