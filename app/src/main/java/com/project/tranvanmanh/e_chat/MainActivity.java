package com.project.tranvanmanh.e_chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private android.support.v7.widget.Toolbar toolbar;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private PageAdapter pageAdapter;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fe-Chat");

        //firebase auth init
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }

        //find view viewpage, tablayout
        viewPager = (ViewPager) findViewById(R.id.main_page);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            GoToStartAcitivy();
        }else {
            mUserDatabase.child("online").setValue("true");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void GoToStartAcitivy() {

        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_logout){
            mAuth.getInstance().signOut();
            GoToStartAcitivy();
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }

        if(item.getItemId() == R.id.menu_item_profile){
            Intent iProfile = new Intent(MainActivity.this, ManageProfileActivity.class);
            startActivity(iProfile);
        }

        if (item.getItemId() == R.id.menu_item_users){

            Intent iUsers = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(iUsers);
        }
        return super.onOptionsItemSelected(item);
    }
}
