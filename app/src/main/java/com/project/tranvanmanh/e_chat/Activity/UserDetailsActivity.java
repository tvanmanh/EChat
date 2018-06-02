package com.project.tranvanmanh.e_chat.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tranvanmanh.e_chat.R;
import com.project.tranvanmanh.e_chat.UserProfile;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {


    private CircleImageView circleImageView;
    private TextView tvName, tvStatus, tvSchool, tvJob, tvMutualFriends;
    private Button btnAddFriend, btnDecline;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseRequest;
    private DatabaseReference mDatabaseFriend;
    private DatabaseReference mDatabaseNotification;
    private FirebaseAuth mAuth;
    private FirebaseUser myUser;


    private String friend ="not_friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        circleImageView = (CircleImageView) findViewById(R.id.detail_imvimage);
        tvName = (TextView) findViewById(R.id.detail_tvname);
        tvStatus = (TextView) findViewById(R.id.detail_status);
        tvSchool = (TextView) findViewById(R.id.detail_tvschool);
        tvJob = (TextView) findViewById(R.id.detail_tvjob);
        tvMutualFriends = (TextView) findViewById(R.id.detail_tvmutualfriend);
        btnAddFriend = (Button) findViewById(R.id.detail_btnAddfriend);
        btnDecline = (Button) findViewById(R.id.detail_btnDecline);


        final String uid = getIntent().getStringExtra("key");

        //database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mDatabaseRequest = FirebaseDatabase.getInstance().getReference().child("req_friend");
        mDatabaseFriend = FirebaseDatabase.getInstance().getReference().child("friend");
        mDatabaseNotification = FirebaseDatabase.getInstance().getReference().child("notification");

        // get current user
        mAuth = FirebaseAuth.getInstance();
        myUser = mAuth.getCurrentUser();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserProfile user = dataSnapshot.getValue(UserProfile.class);

                    tvName.setText(user.getName());
                    tvStatus.setText(user.getStatus());
                    tvJob.setText(user.getCareer());
                    tvSchool.setText(user.getSchool());
                    Picasso.with(UserDetailsActivity.this).load(user.getImage()).placeholder(R.drawable.user_icon).into(circleImageView);

                    // __________________________get friend request_____________________________

                    mDatabaseRequest.child(myUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(uid)){
                                String req_type = dataSnapshot.child(uid).child("req_type").getValue().toString();
                                if(req_type.equals("recieved")){
                                    friend = "req_recieved";
                                    btnAddFriend.setText("Accept Friend");
                                    btnDecline.setVisibility(View.VISIBLE);
                                    btnDecline.setEnabled(true);
                                }if (req_type.equals("sent")) {
                                    friend = "req_sent";
                                    btnAddFriend.setText("Cancel Request");
                                    btnDecline.setVisibility(View.INVISIBLE);
                                    btnDecline.setEnabled(false);
                                }
                            }else{
                                mDatabaseFriend.child(myUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(uid)) {
                                            friend = "friend";
                                            btnAddFriend.setText("unFreind");
                                            btnDecline.setVisibility(View.INVISIBLE);
                                            btnDecline.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFriend.setEnabled(false);
                //    __________________________ not friend _______________________                  //
                if (friend.equals("not_friend")) {
                    mDatabaseRequest.child(myUser.getUid()).child(uid).child("req_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDatabaseRequest.child(uid).child(myUser.getUid()).child("req_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String, String> infoNo = new HashMap<>();
                                        infoNo.put("from", myUser.getUid());
                                        infoNo.put("type", "request");
                                        mDatabaseNotification.child(uid).push().setValue(infoNo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                btnAddFriend.setEnabled(true);
                                                friend = "req_sent";
                                                btnAddFriend.setText("Cancel Friend Request");
                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);
                                                Toast.makeText(UserDetailsActivity.this, "send request successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            } else {
                                Toast.makeText(UserDetailsActivity.this, "error: failed to send request, try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //      _______________________ cancel request _______________________________//

                if(friend.equals("req_sent")){
                    mDatabaseRequest.child(myUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRequest.child(uid).child(myUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDatabaseNotification.child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            btnAddFriend.setEnabled(true);
                                            friend = "not_friend";
                                            btnAddFriend.setText("Add Freind");
                                            btnDecline.setVisibility(View.INVISIBLE);
                                            btnDecline.setEnabled(false);
                                            Toast.makeText(UserDetailsActivity.this, "you have already cancelled request", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                //    __________________________  Accept Friends _________________________//

                if(friend.equals("req_recieved")){
                    final String date = DateFormat.getDateInstance().format(new Date());
                    mDatabaseFriend.child(myUser.getUid()).child(uid).child("date").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseFriend.child(uid).child(myUser.getUid()).child("date").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDatabaseRequest.child(myUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabaseRequest.child(uid).child(myUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDatabaseNotification.child(myUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            btnAddFriend.setEnabled(true);
                                                            friend = "friend";
                                                            btnAddFriend.setText("unFreind");
                                                            btnDecline.setVisibility(View.INVISIBLE);
                                                            btnDecline.setEnabled(false);
                                                            Toast.makeText(UserDetailsActivity.this, "you have already cancelled request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }


                //z-------------------------------- UNFRIEND ------------------------------------//

                if(friend.equals("friend")){
                    mDatabaseFriend.child(myUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseFriend.child(uid).child(myUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnAddFriend.setEnabled(true);
                                    friend = "not_friend";
                                    btnAddFriend.setText("Add Friend");
                                    Toast.makeText(UserDetailsActivity.this, "you have already unfriended", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
