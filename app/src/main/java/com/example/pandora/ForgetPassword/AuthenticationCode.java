package com.example.pandora.ForgetPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pandora.R;

public class AuthenticationCode extends AppCompatActivity {
    String codeAcp = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication_code);

        Intent intent = getIntent();
        String codeAcp = intent.getStringExtra("code");
//        if (getIntent() != null && getIntent().getExtras() != null) {
//            codeAcp = getIntent().getStringExtra("code");
//        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        ActivityResultLauncher<Intent> yourIntent = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult o) {
//                        if (o.getResultCode()==RESULT_OK)
//                        {
//                            Intent intent = o.getData();
//                            if (intent!=null)
//                            {
//                                Bundle bundle = intent.getExtras();
//                                codeAcp = bundle.getString("code");
//                            }
//                        }
//                    }
//                });
        EditText acpCode = findViewById(R.id.acpCode);
        Button btnAcp = findViewById(R.id.btnAcp);

        btnAcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeAcp.equals( acpCode.getText().toString()))
                {
                    Intent myIntent = new Intent(AuthenticationCode.this, ChangePassword.class);


                    startActivity(myIntent);

                }
//                Toast.makeText(getApplicationContext(), "asdasdasdas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}