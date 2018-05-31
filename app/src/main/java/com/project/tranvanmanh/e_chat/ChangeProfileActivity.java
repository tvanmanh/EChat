package com.project.tranvanmanh.e_chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChangeProfileActivity extends AppCompatActivity {

    final static int REQUEST_CODE_IMAGE = 123;
    private CircleImageView circleImageView;
    private EditText edtStatus;
    private EditText edtCareer;
    private EditText edtSchool;
    private EditText edtAchivement;
    private EditText edtHomeAddress;
    private Button btnSaveChanges;
    private TextView tvChangeAvatar;

    private StorageReference mstorageReference;
    private DatabaseReference mDatabase;

    private FirebaseUser mUser;
    private UserProfile user;

    private ProgressDialog mProgress;

    private String path;
    byte[] dataBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        initView();
        user = Parcels.unwrap(getIntent().getParcelableExtra("data"));
        if(user != null){
            edtStatus.setText(user.getStatus());
            edtAchivement.setText(user.getAchivement());
            edtCareer.setText(user.getCareer());
            edtSchool.setText(user.getSchool());
            edtHomeAddress.setText(user.getHome_address());
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uId);
        mstorageReference = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Change profile");
        mProgress.setMessage("please wait ...");
        mProgress.setCanceledOnTouchOutside(false);


        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                mDatabase.child("status").setValue(edtStatus.getText().toString());
                mDatabase.child("achivement").setValue(edtAchivement.getText().toString());
                mDatabase.child("career").setValue(edtCareer.getText().toString());
                mDatabase.child("school").setValue(edtSchool.getText().toString());
                mDatabase.child("home_address").setValue(edtHomeAddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            Intent iManage = new Intent(ChangeProfileActivity.this, ManageProfileActivity.class);
                            startActivity(iManage);
                            finish();
                        }else {
                            Toast.makeText(ChangeProfileActivity.this, "error: saving changes, please check and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tvChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPickImage = new Intent(Intent.ACTION_GET_CONTENT);
                iPickImage.setType("image/*");
                startActivityForResult(Intent.createChooser(iPickImage,"PICK iMAGE"), REQUEST_CODE_IMAGE );
            }
        });
    }

    private void initView(){

        circleImageView = (CircleImageView) findViewById(R.id.manage_imvusericon);

        edtStatus = (EditText) findViewById(R.id.change_tvsaying);
        edtAchivement = (EditText) findViewById(R.id.change_tvachivement);
        edtCareer = (EditText) findViewById(R.id.change_tvcareer);
        edtSchool = (EditText) findViewById(R.id.change_tvschool);
        edtHomeAddress = (EditText) findViewById(R.id.change_tvlocation);
        btnSaveChanges = (Button) findViewById(R.id.change_btnchange);
        tvChangeAvatar = (TextView) findViewById(R.id.change_tvchangeavatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress.show();

                Uri resultUri = result.getUri();

                //get bitmap and compress image
                final File thumb_path = new File(resultUri.getPath());
                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(150)
                            .setMaxHeight(150)
                            .setQuality(80)
                            .compressToBitmap(thumb_path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    dataBitmap = baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //store image to the firebase storage
                final StorageReference pathFile = mstorageReference.child("images").child(mUser.getUid()+".jpg");
                final StorageReference path_ThumbFile = mstorageReference.child("images").child("thumbnail").child(mUser.getUid() + "jpg");


                pathFile.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //get path image
                            path = task.getResult().getDownloadUrl().toString();
                            //upload image to storage
                            UploadTask uploadTask = path_ThumbFile.putBytes(dataBitmap);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String path_thumb = task.getResult().getDownloadUrl().toString();
                                        //set image to cirecleview of changeactivity
                                        Picasso.with(ChangeProfileActivity.this).load(path).into(circleImageView);

                                        //update image and thumb_image to database
                                        Map map = new HashMap();
                                        map.put("image", path);
                                        map.put("thumb_image", path_thumb);
                                        mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    mProgress.dismiss();
                                                }else {
                                                    Toast.makeText(ChangeProfileActivity.this, "error: fail uploading image and thumbnail to database", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(ChangeProfileActivity.this, "error: failed uploading thumbnail to storage", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else {
                            Toast.makeText(ChangeProfileActivity.this, "error: upload image to storage", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
