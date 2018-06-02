package com.project.tranvanmanh.e_chat.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.tranvanmanh.e_chat.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;

    private Button btnLogin;

    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.log_edtemail);
        edtPassword = (EditText) findViewById(R.id.log_edtpassword);
        btnLogin = (Button) findViewById(R.id.log_btnlogin);

        toolbar = (Toolbar) findViewById(R.id.log_toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login To Your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String passowrd = edtPassword.getText().toString();

                if(!email.isEmpty()&&!passowrd.isEmpty()){
                    progressDialog.setTitle("Logining user");
                    progressDialog.setTitle("please wait ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    LoginAcount(email, passowrd);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Warning: Miss some information, please check and try again!", Toast.LENGTH_SHORT).show();
                }
            }

            private void LoginAcount(String email, String passowrd) {

                mAuth.signInWithEmailAndPassword(email, passowrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();

                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String curent_id_user = mAuth.getCurrentUser().getUid();
                            mUserDatabase.child(curent_id_user).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, "error: sign in, please check your acount and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
