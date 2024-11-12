package com.example.pandora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.telephony.SmsManager;
import android.widget.Toast;


import org.jetbrains.annotations.NonNls;

import java.util.Random;

public class ForgetPassword extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button acp = findViewById(R.id.btnAcp);
        EditText numberPhone = findViewById(R.id.numberPhone);
        Random rd = new Random();
        acp.setOnClickListener(view -> {
            Integer code = 100000 + rd.nextInt(900000);
//                if (ActivityCompat.checkSelfPermission(ForgetPassword.this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
//                {
//                    ActivityCompat.requestPermissions(ForgetPassword.this,new String[]{Manifest.permission.SEND_SMS},SMS_PERMISSION_CODE);
//                }
//                else
//                {
//                    sendSms(numberPhone.getText().toString(), Integer.toString(code));
//                }
            String s= code.toString();
            Toast.makeText(ForgetPassword.this, s, Toast.LENGTH_LONG).show();


            Intent myIntent = new Intent(ForgetPassword.this, AuthenticationCode.class);
            myIntent.putExtra("code",s);
            startActivity(myIntent);
        });
    }
    private void sendSms(String phoneNumeber,String message)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumeber,null,message,null,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==SMS_PERMISSION_CODE)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
        }
    }
}