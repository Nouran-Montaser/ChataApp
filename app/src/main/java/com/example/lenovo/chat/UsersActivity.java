package com.example.lenovo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUsersDatabaseRef;
    private ArrayList<Users> details;
    private adapter myAdapter;
    private FirebaseUser mCurrentUser;
//    private Query user;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mToolbar = findViewById(R.id.users_page_toobar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_Uid = mCurrentUser.getUid();
        mUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);

        details = new ArrayList<>();
        mUserList = findViewById(R.id.Users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        mUsersDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Toast.makeText(UsersActivity.this ,snapshot.getKey() ,Toast.LENGTH_LONG).show();
                    if((snapshot.getKey()).equals(current_Uid)) continue;
                    details.add(new Users(snapshot.getKey(),snapshot.child("name").getValue().toString(),R.drawable.defaultt,
                            snapshot.child("status").getValue().toString()));
                    //break;
                }
                myAdapter = new adapter(UsersActivity.this, details,"Users");
                mUserList.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser != null) {
            final String current_Uid = mCurrentUser.getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);
            mUserRef.child("online").setValue("true");
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            databaseReference.child(mAuth.getCurrentUser().getUid());
//            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mUserRef.child("online").setValue("true");
//    }
}
