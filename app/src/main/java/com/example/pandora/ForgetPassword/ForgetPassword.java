package com.example.pandora.ForgetPassword;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;


import com.example.pandora.AdminProperties.AdminProperties;
import com.example.pandora.Class.User;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.R;

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
            int code = 100000 + rd.nextInt(900000);
                if (ActivityCompat.checkSelfPermission(ForgetPassword.this, Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(ForgetPassword.this,new String[]{Manifest.permission.SEND_SMS},SMS_PERMISSION_CODE);
                }
            UserDatabase db= new UserDatabase(ForgetPassword.this);
                db.open();
                User u= db.getUserBySDT(numberPhone.getText().toString());
                db.close();
            if(u!=null){

                sendSms(numberPhone.getText().toString(), String.valueOf(code));
                Toast.makeText(getApplicationContext(),String.valueOf(code), Toast.LENGTH_LONG).show();

//            String s= String.valueOf(code);
//            Toast.makeText(ForgetPassword.this, s, Toast.LENGTH_LONG).show();


                Intent myIntent = new Intent(ForgetPassword.this, AuthenticationCode.class);
                myIntent.putExtra("code",code);
                myIntent.putExtra("userid", u.getId());
                startActivity(myIntent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Sdt không hợp le", Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void sendSms(String phoneNumeber,String message)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumeber,null,message,null,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền gửi SMS đã được cấp.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền gửi SMS để sử dụng chức năng này.", Toast.LENGTH_LONG).show();
            }
        }
    }

}