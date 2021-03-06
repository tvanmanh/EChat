package com.project.tranvanmanh.e_chat.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.tranvanmanh.e_chat.Activity.DisplayImageActivity;
import com.project.tranvanmanh.e_chat.Model.Message;
import com.project.tranvanmanh.e_chat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private ArrayList<Message> messages;
    private String mMyUser_id;
    private String thumb_friend_image;
    private String thumb_my_image;

    public MessageAdapter(ArrayList<Message> messages, String myUser_id, String thumb_friend_image, DatabaseReference mDatabase) {
        this.messages = messages;
        this.mMyUser_id = myUser_id;
        this.thumb_friend_image = thumb_friend_image;
        mDatabase.child("users").child(myUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thumb_my_image = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("thumb_friend", thumb_friend_image );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_message_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Message message = messages.get(position);
        String from_id = message.getFrom();
        if(from_id.equals(mMyUser_id)){

            holder.tvDisplayMessage.setVisibility(View.GONE);
            holder.imvAddImage.setVisibility(View.GONE);
            holder.crlMessage.setVisibility(View.GONE);

            holder.tvDisplayMessageM.setVisibility(View.VISIBLE);
            holder.imvAddImageM.setVisibility(View.VISIBLE);
            holder.crlMessageM.setVisibility(View.GONE);

            holder.tvDisplayMessageM.setTextColor(Color.WHITE);
            holder.tvDisplayMessageM.setBackgroundResource(R.drawable.shape_message);
            Picasso.with(holder.itemView.getContext()).load(thumb_my_image).placeholder(R.drawable.user_icon).into(holder.crlMessageM);

            if((message.getType()).equals("text")) {
                holder.tvDisplayMessageM.setText(message.getMessage());
                holder.imvAddImageM.setVisibility(View.GONE);
                holder.tvDisplayMessageM.setVisibility(View.VISIBLE);

            } else {
                holder.tvDisplayMessageM.setVisibility(View.GONE);
                holder.imvAddImageM.setVisibility(View.VISIBLE);
                Picasso.with(holder.itemView.getContext()).load(message.getMessage()).placeholder(R.drawable.loadingimage_ic)
                        .into(holder.imvAddImageM);
                holder.imvAddImageM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iDisplayImage = new Intent(holder.itemView.getContext(), DisplayImageActivity.class);
                        iDisplayImage.putExtra("image", message.getMessage());
                        holder.itemView.getContext().startActivity(iDisplayImage);
                    }
                });
            }
        }else {
            holder.tvDisplayMessage.setVisibility(View.VISIBLE);
            holder.imvAddImage.setVisibility(View.VISIBLE);
            holder.crlMessage.setVisibility(View.VISIBLE);

            holder.tvDisplayMessageM.setVisibility(View.GONE);
            holder.imvAddImageM.setVisibility(View.GONE);
            holder.crlMessageM.setVisibility(View.GONE);

            holder.tvDisplayMessage.setTextColor(Color.BLACK);
            holder.tvDisplayMessage.setBackgroundResource(R.drawable.shape_friend_message);
            Picasso.with(holder.itemView.getContext()).load(thumb_friend_image).placeholder(R.drawable.user_icon).into(holder.crlMessage);

            if((message.getType()).equals("text")) {
                holder.tvDisplayMessage.setText(message.getMessage());
                holder.imvAddImage.setVisibility(View.GONE);
                holder.tvDisplayMessage.setVisibility(View.VISIBLE);

            } else {
                holder.tvDisplayMessage.setVisibility(View.GONE);
                holder.imvAddImage.setVisibility(View.VISIBLE);
                Picasso.with(holder.itemView.getContext()).load(message.getMessage()).placeholder(R.drawable.loadingimage_ic)
                        .into(holder.imvAddImage);
                holder.imvAddImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iDisplayImage = new Intent(holder.itemView.getContext(), DisplayImageActivity.class);
                        iDisplayImage.putExtra("image", message.getMessage());
                        holder.itemView.getContext().startActivity(iDisplayImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDisplayMessage;
        private CircleImageView crlMessage;
        private ImageView imvAddImage;
        private TextView tvDisplayMessageM;
        private CircleImageView crlMessageM;
        private ImageView imvAddImageM;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDisplayMessage = (TextView) itemView.findViewById(R.id.tvDiplayContent);
            crlMessage = (CircleImageView) itemView.findViewById(R.id.message_image);
            imvAddImage = (ImageView) itemView.findViewById(R.id.imvImage);
            tvDisplayMessageM = (TextView) itemView.findViewById(R.id.tvDiplayContentm);
            crlMessageM = (CircleImageView) itemView.findViewById(R.id.message_imagem);
            imvAddImageM = (ImageView) itemView.findViewById(R.id.imvImagem);
        }
    }
}
