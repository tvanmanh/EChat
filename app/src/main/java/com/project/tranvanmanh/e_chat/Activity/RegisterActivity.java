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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.tranvanmanh.e_chat.R;
import com.project.tranvanmanh.e_chat.UserProfile;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPass;
    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Toolbar toolbar;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        // init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //progressdialog init
        progressDialog = new ProgressDialog(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String confirmpass = edtConfirmPass.getText().toString();
                String email = edtEmail.getText().toString();

                progressDialog.setTitle("Registering user");
                progressDialog.setMessage("please wait....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Register(username, email, password, confirmpass);

            }
        });
    }

    private void Register(final String username, String email, String password, String confirmpass){

        if(!username.equals("")&&!email.equals("")&&!password.equals("")&&!confirmpass.equals("")){
            if(password.equals(confirmpass)){

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uId = firebaseUser.getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            UserProfile user = new UserProfile();
                            user.setThumb_image("default");
                            user.setName(username);
                            user.setStatus("your status");
                            user.setAchivement("your Achivement");
                            user.setCareer("your career");
                            user.setSchool("your school");
                            user.setHome_address("your home address");
                            user.setImage("your image");
                            user.setDevice_token(deviceToken);


                            mDatabase.child("users").child(uId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent regi = new Intent(RegisterActivity.this, MainActivity.class);
                                        regi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(regi);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            progressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "error when registing acount, check the form and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else {
                Toast.makeText(this, "error: password is incorrect!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "error: miss some information!", Toast.LENGTH_SHORT).show();
        }
    }
    private void init(){

        edtUsername = (EditText) findViewById(R.id.reg_edtusername);
        edtEmail = (EditText) findViewById(R.id.reg_edtemail);
        edtPassword = (EditText) findViewById(R.id.reg_edtpassword);
        edtConfirmPass = (EditText) findViewById(R.id.reg_edtconfirmpass);

        btnSubmit = (Button) findViewById(R.id.reg_btnsubmit);

        toolbar = (Toolbar) findViewById(R.id.reg_toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create A New Acount");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
