package com.project.tranvanmanh.e_chat.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.tranvanmanh.e_chat.R;

public class StartActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnRegister = (Button) findViewById(R.id.start_btnReg);
        btnLogin = (Button) findViewById(R.id.start_btnLogin);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iReg = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(iReg);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLog = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(iLog);
            }
        });
    }
}
