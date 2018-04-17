package com.example.lenovo.chat;

import android.content.Context;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mToolbar;
    private DatabaseReference mUserDataBase;
    private String mChatUserName;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImg;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    private ImageView mChatAddBtn;
    private ImageButton mChatSendBtn;
    private hani.momanii.supernova_emoji_library.Helper.EmojiconEditText mChatMessageView;
    private RecyclerView mMessagesist;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final List<Messages> messageList = new ArrayList<Messages>() {
    };
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mAdapter;
    private DatabaseReference mMessageDatabase;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevtKey = "";
    private View v;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mUserDataBase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_ID");
        mChatUserName = getIntent().getStringExtra("user_Name");

        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDataBase.child("User").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("name").getValue().toString();
                mTitleView.setText(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(actionBarView);


        //---------- Custom Action Bar Items ----

        mTitleView = findViewById(R.id.custom_bar_title);
        mLastSeenView = findViewById(R.id.Custom_bar_lastseen);
        mProfileImg = findViewById(R.id.customBarImg);


        mChatAddBtn = findViewById(R.id.ChatAddButton);
        mChatSendBtn = findViewById(R.id.ChatSendButton);
        mChatMessageView = findViewById(R.id.ChatMessageView);


        mMessagesist = findViewById(R.id.message_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_message);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mMessagesist.setHasFixedSize(true);
        mMessagesist.setLayoutManager(mLinearLayoutManager);
        mAdapter = new MessageAdapter(messageList, mCurrentUserID);
        mMessagesist.setAdapter(mAdapter);
        loadMessages();


        mUserDataBase.child("User").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mOnline = dataSnapshot.child("online").getValue().toString();
                if (mOnline.equals("true"))
                    mLastSeenView.setText("Online");
                else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long mLastTime = Long.parseLong(mOnline);
                    String mLastSeenTime = getTimeAgo.getTimeAgo(mLastTime, getApplicationContext());
                    mLastSeenView.setText(mLastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        getSupportActionBar().setTitle(mChatUserName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mUserDataBase.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatMap = new HashMap();
                    chatMap.put("seen", false);
                    chatMap.put("timeStamp", ServerValue.TIMESTAMP);
                    //here

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mChatUser, chatMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserID, chatMap);
                    mUserDataBase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("Chat Log", databaseError.getMessage().toString());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        v = findViewById(R.id.rootid);
        EmojIconActions emojIcon=new EmojIconActions(this,v,mChatMessageView,mChatAddBtn,"#495C66","#DCE1E2","#E6EBEF");
        emojIcon.ShowEmojIcon();
        
//        mChatMessageView.setFocusable(false);
//        mChatMessageView.setFocusableInTouchMode(true);


//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
//        emojIcon.addEmojiconEditTextList(emojiconEditText2);

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });


    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mUserDataBase.child("Messages").child(mCurrentUserID).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                String mMessageKey = dataSnapshot.getKey();


                if(! mPrevtKey.equals(mMessageKey))
                {
                    messageList.add(itemPos++, messages);
                }
                else
                {
                    mPrevtKey = mLastKey ;
                }

                if (itemPos == 1) {
                    mLastKey = mMessageKey;
                }


                Log.d("key", "LastKey : " + mLastKey + " | prev Key : " + mPrevtKey + " | MessageKey :" + mMessageKey);

                mAdapter.notifyDataSetChanged();

                mMessagesist.scrollToPosition(messageList.size() - 1);

                mSwipeRefreshLayout.setRefreshing(false);
                mLinearLayoutManager.scrollToPositionWithOffset(10, 0);

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

    private void loadMessages() {

        DatabaseReference messageRef = mUserDataBase.child("Messages").child(mCurrentUserID).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage = TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);

                if (itemPos == 1) {
                    String mMessageKey = dataSnapshot.getKey();
                    mLastKey = mMessageKey;
                    mPrevtKey = mMessageKey;
                }

                itemPos++;

                messageList.add(messages);
                mAdapter.notifyDataSetChanged();

                mMessagesist.scrollToPosition(messageList.size() - 1);

                mSwipeRefreshLayout.setRefreshing(false);

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

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "Messages/" + mCurrentUserID + "/" + mChatUser;
            String chat_user_ref = "Messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference userMessageRef = mUserDataBase.child("Messages").child(mCurrentUserID).child(mChatUser).push();

            String puch_id = userMessageRef.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("send", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserID);
            messageMap.put("name", mChatUserName );



            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + puch_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + puch_id, messageMap);

            mChatMessageView.setText("");

            mUserDataBase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat Log", databaseError.getMessage().toString());
                    }
                }
            });

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            final String current_Uid = mCurrentUser.getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);
            mUserRef.child("online").setValue("true");
        }
    }


}
