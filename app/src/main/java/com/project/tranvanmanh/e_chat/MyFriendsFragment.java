package com.project.tranvanmanh.e_chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tranvanmanh on 4/26/2018.
 */

public class MyFriendsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mMyUserId;

    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_my_friends, container, false);

        mRecyclerview = view.findViewById(R.id.friend_recyclerview);

        mAuth = FirebaseAuth.getInstance();
        mMyUserId = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friend").child(mMyUserId);
        mFriendDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.keepSynced(true);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerview.setHasFixedSize(true);


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, ViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, ViewHolder>
                (
                        Friends.class,
                        R.layout.users_item_layout,
                        ViewHolder.class,
                        mFriendDatabase
                ) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final Friends model, int position) {
                viewHolder.setDate(model.getDate());

                final String friend_id = getRef(position).getKey();
                mUserDatabase.child(friend_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot != null){
                           final String name = dataSnapshot.child("name").getValue().toString();
                           final String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                           if(dataSnapshot.hasChild("online")){
                               String online = dataSnapshot.child("online").getValue().toString();
                               viewHolder.setOnline(online);
                           }
                           viewHolder.setImage(thumbImage);
                           viewHolder.setName(name);
                           viewHolder.layout.setBackgroundColor(Color.WHITE);
                           viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   CharSequence[] items = new CharSequence[]{"About " + name, "Send Message"};
                                   AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                   builder.setItems(items, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           switch (which){
                                               case 0:
                                                   Intent iProfile = new Intent(getContext(), UserDetailsActivity.class);
                                                   iProfile.putExtra("key", friend_id);
                                                   startActivity(iProfile);
                                                   break;
                                               case 1:
                                                   Intent iMessage = new Intent(getContext(), ChatActivity.class);
                                                   iMessage.putExtra("user_id", friend_id);
                                                   iMessage.putExtra("name", name);
                                                   iMessage.putExtra("thumb_image", thumbImage);
                                                   startActivity(iMessage);
                                                   break;
                                           }
                                       }
                                   });
                                   builder.show();
                               }
                           });

                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mRecyclerview.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate;
        TextView tvName;
        CircleImageView circleImageView;
        ImageView imvOnline;
        RelativeLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.users_item_status);
            tvName = (TextView) itemView.findViewById(R.id.users_item_name);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.users_item_image);
            imvOnline = (ImageView) itemView.findViewById(R.id.online_ic);
            layout = (RelativeLayout)itemView.findViewById(R.id.itembackground);

        }

        public void setDate(String date){
            tvDate.setText(date);
        }

        public void setName(String name){
            tvName.setText(name);
        }

        public void setImage(String url){
            Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.user_icon).into(circleImageView);
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
