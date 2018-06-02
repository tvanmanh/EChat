package com.project.tranvanmanh.e_chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private TextView tvName;
    private TextView tvTimeOffline;
    private CircleImageView mBarCirclerImageView;


    private String friend_id;
    private String myUser_id;
    private EditText edtContent;
    private ImageView imvSend;
    private ImageView imvAddImage;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayout;
    private ArrayList<Message> messages;
    private MessageAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final static int TOTAL_MESSAGE_LOADING = 10;
    private int load_Next_Item = 1;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;


    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        friend_id = bundle.getString("user_id");
        String name = bundle.getString("name");
        final String thumb_image = bundle.getString("thumb_image");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        myUser_id = mAuth.getCurrentUser().getUid();

        mToolbar = (Toolbar) findViewById(R.id.chat_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(view);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        tvName = (TextView) findViewById(R.id.displayname);
        tvTimeOffline = (TextView) findViewById(R.id.time);
        mBarCirclerImageView = (CircleImageView) findViewById(R.id.bar_image);

        edtContent = (EditText) findViewById(R.id.edtContent);
        imvSend = (ImageView) findViewById(R.id.imvSend);
        imvAddImage = (ImageView) findViewById(R.id.addImage);

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler);
        mLinearLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayout);
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, myUser_id, thumb_image, mDatabase);
        mRecyclerView.setAdapter(adapter);
        loadMessage();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        tvName.setText(name);
        Picasso.with(this).load(thumb_image).placeholder(R.drawable.user_icon).into(mBarCirclerImageView);

        mDatabase.child("users").child(friend_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                       if(dataSnapshot.hasChild("online")) {
                           String online = dataSnapshot.child("online").getValue().toString();
                           if (online.equalsIgnoreCase("true")) {
                               tvTimeOffline.setText("online");
                           } else {
                               Long time = Long.parseLong(online);
                               String timeAgo = TimeAgo.getTimeAgo(time);
                               tvTimeOffline.setText(timeAgo);
                           }
                       }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child("chat").child(myUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(friend_id)){
                    Map addChat = new HashMap();
                    addChat.put("seen", false);
                    addChat.put("time", ServerValue.TIMESTAMP);

                    Map userChat = new HashMap();
                    userChat.put("chat/" + myUser_id + "/" + friend_id, addChat);
                    userChat.put("chat/" + friend_id + "/" + myUser_id, addChat);

                    mDatabase.updateChildren(userChat, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                edtContent.setText("");
            }
        });

        imvAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_Next_Item ++;
                itemPos = 0;
                loadMoreMessage();
            }
        });
    }

    private  void sendMessage(){
        String message = edtContent.getText().toString().trim();
        if(!message.isEmpty()){
            DatabaseReference mDatabaseMessage = mDatabase.child("messages").child(myUser_id).child(friend_id).push();
            String push_id = mDatabaseMessage.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", myUser_id);
            Map addMessage = new HashMap();
            addMessage.put("message/" + myUser_id + "/" + friend_id +"/" + push_id, messageMap);
            addMessage.put("message/" + friend_id + "/" + myUser_id + "/" + push_id, messageMap);

            mDatabase.updateChildren(addMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("Error_chat", databaseError.getMessage().toString());
                    }
                }
            });
        }

    }

    private void loadMoreMessage() {

        mSwipeRefreshLayout.setRefreshing(false);

        DatabaseReference messageRef = mDatabase.child("message").child(myUser_id).child(friend_id);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messages.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                adapter.notifyDataSetChanged();

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void loadMessage(){
        DatabaseReference mLoadMessageRef = mDatabase.child("message").child(myUser_id).child(friend_id);
        Query mQueyRef = mLoadMessageRef.limitToLast(TOTAL_MESSAGE_LOADING*load_Next_Item);
         mQueyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    Message message = dataSnapshot.getValue(Message.class);
                    itemPos++;
                    if(itemPos == 1){

                        String messageKey = dataSnapshot.getKey();

                        mLastKey = messageKey;
                        mPrevKey = messageKey;

                    }
                    messages.add(message);
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(messages.size() -1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.child("chat").child(myUser_id).child(friend_id).child("time").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_user_ref = "message/" + myUser_id + "/" + friend_id;
            final String chat_user_ref = "message/" + friend_id + "/" + myUser_id;

            DatabaseReference user_message_push = mDatabase.child("message")
                    .child(myUser_id).child(friend_id).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", myUser_id);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        edtContent.setText("");

                        mDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });


                    }

                }
            });

        }

    }
}
