package com.project.tranvanmanh.e_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DisplayImageActivity extends AppCompatActivity {

    private ImageView imvDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imvDisplay = (ImageView) findViewById(R.id.display_image);
        String imageUrl = getIntent().getStringExtra("image");
        Picasso.with(this).load(imageUrl).into(imvDisplay);
    }
}
