package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Setting extends Fragment {

    public Setting() {
        // Required empty public constructor
    }

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container, false);
        TextView edtPolicy = view.findViewById(R.id.policy);
        edtPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditPolicyDialog();
            }
        });

        TextView edtLanguage = view.findViewById(R.id.language);
        edtLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditLanguageDialog();
            }
        });

        return view;
    }
    private void showEditPolicyDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_policy_setting, null);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showEditLanguageDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_language_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}