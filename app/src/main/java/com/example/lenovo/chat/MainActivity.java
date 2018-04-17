package com.example.lenovo.chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_toobar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat App");

        mViewPager = findViewById(R.id.tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());//Return the FragmentManager for interacting with fragments associated with this activity.
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendToStart();
        }
        else
        {
            userID=mAuth.getCurrentUser().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
            mUserRef.child("online").setValue("true");
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null)
//        {
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
////            mUserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null)
//        {
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
////            mUserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
//        }
//    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this , StartActivity.class);
        startActivity(startIntent);
        finish();//when we are don't need to go back (no back button)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_button)
        {
            FirebaseAuth.getInstance().signOut();
          //  Toast.makeText(MainActivity.this,"CDd",Toast.LENGTH_SHORT).show();
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            sendToStart();
        }
        else if(item.getItemId() == R.id.AccountSettings_button)
        {
            Intent settingIntent = new Intent(MainActivity.this ,SettingActivity.class);
            startActivity(settingIntent);
        }
        else if(item.getItemId() == R.id.Allusers_button)
        {
            Intent settingIntent = new Intent(MainActivity.this ,UsersActivity.class);
            startActivity(settingIntent);
        }

        return true;
    }
}
