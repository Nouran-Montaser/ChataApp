package com.example.lenovo.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestFragment extends Fragment {

    private RecyclerView mRequestsList;
    private DatabaseReference mRequestsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private ArrayList<Requests> details;
    private RequestAdapter myAdapter;
    private DatabaseReference mUserDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        mRequestsList = mMainView.findViewById(R.id.request_listt);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(mCurrent_user_id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        details = new ArrayList<>();
        mRequestsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        String id = snapshot.getKey();
                        String name = snapshot.child("name").getValue().toString();
                        String type = snapshot.child("request_type").getValue().toString();
                        details.add(new Requests(id, name, type));
                    }
                    myAdapter = new RequestAdapter(getContext(), details);
                    mRequestsList.setAdapter(myAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
