package com.project.tranvanmanh.e_chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView = (RecyclerView) findViewById(R.id.users_recyclerview);

        toolbar = (Toolbar) findViewById(R.id.users_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<users, RecyclerviewHolder> adapter = new FirebaseRecyclerAdapter<users, RecyclerviewHolder>(
                users.class,
                R.layout.users_item_layout,
                RecyclerviewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(RecyclerviewHolder viewHolder, users model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getThumb_image(), getApplicationContext());

                final String uid_user = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iUserDetail = new Intent(UsersActivity.this, UserDetailsActivity.class);
                        iUserDetail.putExtra("key", uid_user);
                        startActivity(iUserDetail);

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);


    }

    public static class RecyclerviewHolder extends RecyclerView.ViewHolder{

        View mView;
        public RecyclerviewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){

            TextView userName = (TextView)mView.findViewById(R.id.users_item_name);
            userName.setText(name);
        }

        public void setStatus(String status){

            TextView userStatus = (TextView) mView.findViewById(R.id.users_item_status);
            userStatus.setText(status);
        }

        public void setImage(String thumb_image, Context context){

            CircleImageView circleImageView = (CircleImageView) mView.findViewById(R.id.users_item_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.user_icon).into(circleImageView);
        }
    }
}
