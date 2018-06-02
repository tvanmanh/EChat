package com.project.tranvanmanh.e_chat.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tranvanmanh.e_chat.R;
import com.project.tranvanmanh.e_chat.UserProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageProfileActivity extends AppCompatActivity {


    private CircleImageView circleImageView;
    private TextView tvName;
    private TextView tvStatus;
    private TextView tvCareer;
    private TextView tvSchool;
    private TextView tvAchivement;
    private TextView tvHomeAddress;
    private TextView tvchange;
    private UserProfile user;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        initView();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uId);
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(UserProfile.class);
                    tvName.setText(user.getName());
                    tvStatus.setText(user.getStatus());
                    tvAchivement.setText(user.getAchivement());
                    tvCareer.setText(user.getCareer());
                    tvSchool.setText(user.getSchool());
                    tvHomeAddress.setText(user.getHome_address());

                    if(user.getImage() != null) {
                        if(!user.getImage().equals("your image"))
                        Picasso.with(ManageProfileActivity.this).load(user.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_icon).into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(ManageProfileActivity.this).load(user.getImage()).placeholder(R.drawable.user_icon).into(circleImageView);

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iChange = new Intent(ManageProfileActivity.this, ChangeProfileActivity.class);
                iChange.putExtra("data", Parcels.wrap(user));
                startActivity(iChange);
                finish();
            }
        });
    }

    private void initView(){

        circleImageView = (CircleImageView) findViewById(R.id.manage_imvusericon);

        tvName = (TextView) findViewById(R.id.manage_tvname);
        tvStatus = (TextView) findViewById(R.id.manage_tvsaying);
        tvAchivement = (TextView) findViewById(R.id.manage_tvachivement);
        tvCareer = (TextView) findViewById(R.id.manage_tvcareer);
        tvSchool = (TextView) findViewById(R.id.manage_tvschool);
        tvHomeAddress = (TextView) findViewById(R.id.manage_tvlocation);
        tvchange = (TextView) findViewById(R.id.manage_tvchange);
    }
}
