package com.example.lenovo.chat;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
//import com.vanniktech.emoji.EmojiManager;
//import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class MyApplication extends Application {

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        // This instantiates DBFlow
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FlowManager.init(new FlowConfig.Builder(this).build());
        // add for verbose logging
        // FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

 //       EmojiManager.install(new GoogleEmojiProvider()); // This line needs to be executed before any usage of EmojiTextView, EmojiEditText or EmojiButton.

        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(this, "" + mAuth, Toast.LENGTH_SHORT).show();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            mCurrentUser=mAuth.getCurrentUser().getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        //mUserDatabase.child("online").setValue(true);
                        //mUserDatabase.child("lastSeen").setValue(ServerValue.TIMESTAMP);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
//        }
    }
}