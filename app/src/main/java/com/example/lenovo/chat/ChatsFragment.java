package com.example.lenovo.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mSMessageDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private ArrayList<Conversation> details;
    private final ArrayList<String> listUser = new ArrayList<>();
    private final ArrayList<Conversation> listSUser = new ArrayList<>();
    private final ArrayList<Conversation> listUsers = new ArrayList<>();
    private ChatAdapter adapter;

    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = mMainView.findViewById(R.id.chats_listt);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);
        mSMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conv = mConvDatabase.orderByChild("time");

        details = new ArrayList<>();

        mMessageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listUser.add(snapshot.getKey());
                }
                for (final String s : listUser) {
                    mSMessageDatabase.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String mMessage = snapshot.child("message").getValue().toString();
//                                Toast.makeText(getContext(),""+snapshot.child("name").getValue(),Toast.LENGTH_LONG).show();
                               // String mName = snapshot.child("name").getValue().toString();
                                long mTime = (long) snapshot.child("time").getValue();
                                String from = snapshot.child("from").getValue().toString();
                                listSUser.add(new Conversation(""+snapshot.child("name").getValue(), mMessage, mTime, false ,from ));
                            }
                            listUsers.add(new Conversation(listSUser.get(listSUser.size() - 1).getUser() ,listSUser.get(listSUser.size() - 1).getMessage(), listSUser.get(listSUser.size() - 1).getTime() , false , listSUser.get(listSUser.size() - 1).getFrom()));
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                adapter = new ChatAdapter(getContext(), listUsers);
                mConvList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}