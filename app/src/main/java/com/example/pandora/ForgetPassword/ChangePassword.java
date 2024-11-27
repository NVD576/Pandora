package com.example.pandora.ForgetPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.Login.Login;
import com.example.pandora.R;

public class ChangePassword extends AppCompatActivity {
    Button btnXacNhan;
    EditText edMK, edMK2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnXacNhan = findViewById(R.id.btnXacNhan);
        edMK= findViewById(R.id.edMK);
        edMK2= findViewById(R.id.edMK2);


        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edMK.getText().toString().equals(edMK2.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Đổi mật khẩu thành công",Toast.LENGTH_SHORT).show();
                    String passUpdate = edMK.getText().toString();
                    updatePassword(passUpdate);
                    Intent intent= new Intent(ChangePassword.this, Login.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Mật khẩu không trùng khớp!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updatePassword(String passUpdate){

    }
}