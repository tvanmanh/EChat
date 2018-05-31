package com.project.tranvanmanh.e_chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tranvanmanh on 4/26/2018.
 */

public class MyMesssagesFragment extends Fragment {

    private View view;
    private RecyclerView mRecyclerView;
    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String myUser_id;
    private String friend_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_my_messages, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.message_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mAuth = FirebaseAuth.getInstance();
        myUser_id = mAuth.getCurrentUser().getUid();
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child(myUser_id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chat, ViewHolder> adapter = new FirebaseRecyclerAdapter<Chat, ViewHolder>
                (
                        Chat.class,
                        R.layout.users_item_layout,
                        ViewHolder.class,
                        mRequestDatabase
                ) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, Chat model, int position) {
                friend_id = getRef(position).getKey();
                Long time = model.getTime();
                String timeAgo = TimeAgo.getTimeAgo(time);
                viewHolder.setDate(timeAgo);
                mUserDatabase.child(friend_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            final String name = dataSnapshot.child("name").getValue().toString();
                            Log.e("name", name );
                            viewHolder.setName(name);
                            final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                            viewHolder.setImage(thumb_image);
                        viewHolder.layout.setBackgroundColor(Color.WHITE);
                            if(dataSnapshot.hasChild("online")){
                                String online = dataSnapshot.child("online").getValue().toString();
                                viewHolder.setOnline(online);
                            }

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iMessage = new Intent(getContext(), ChatActivity.class);
                                iMessage.putExtra("user_id", friend_id);
                                iMessage.putExtra("name", name);
                                iMessage.putExtra("thumb_image", thumb_image);
                                startActivity(iMessage);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime;
        CircleImageView mImage;
        ImageView imvOnline;
        RelativeLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.users_item_name);
            tvTime = (TextView) itemView.findViewById(R.id.users_item_status);
            mImage = (CircleImageView) itemView.findViewById(R.id.users_item_image);
            imvOnline = (ImageView) itemView.findViewById(R.id.online_ic);
            layout = (RelativeLayout)itemView.findViewById(R.id.itembackground);
        }

        public void setDate(String date){
            tvTime.setText(date);
        }

        public void setName(String name){
            tvName.setText(name);
        }

        public void setImage(String url){
            Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.user_icon).into(mImage);
        }

        public void setOnline(String online){
            if(online.equalsIgnoreCase("true")){
                imvOnline.setVisibility(View.VISIBLE);
            }else {
                imvOnline.setVisibility(View.INVISIBLE);
            }
        }
    }
}
