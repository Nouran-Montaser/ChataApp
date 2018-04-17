package com.example.lenovo.chat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn;
    private Button mProfileDeclineReqBtn;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mNotificationsDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserRef;
    private ProgressDialog mProfileProgress;
    private String mCurrentState;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_ID = getIntent().getStringExtra("user_ID");

        mProfileImage = findViewById(R.id.activity_profile_img);
        mProfileName = findViewById(R.id.activity_profile_name);
        mProfileStatus = findViewById(R.id.activity_profile_status);
        mProfileFriendsCount = findViewById(R.id.activity_profile_friends);
        mProfileSendReqBtn = findViewById(R.id.activity_profile_sendReq);
        mProfileDeclineReqBtn = findViewById(R.id.activity_profile_declineReq);

        mCurrentState = "not_friends";

        mProfileProgress = new ProgressDialog(this);
        mProfileProgress.setTitle("Loading User Data ...");
        mProfileProgress.setMessage("Please wait while we load the user data.");
        mProfileProgress.setCanceledOnTouchOutside(false);//prevenr user from touch on screen
        mProfileProgress.show();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference("FriendReq");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference("Friends");
        mNotificationsDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user_ID);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrentUser.getUid());
        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineReqBtn.setEnabled(false);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String displayStatus = dataSnapshot.child("status").getValue().toString();
                //            String displayimage = dataSnapshot.child("image").getValue().toString();

                mProfileImage.setImageResource(R.drawable.defaultt);
                mProfileName.setText(displayName);
                mProfileStatus.setText(displayStatus);

                // - --------------------Friend List / Request Feature -------
                //addListenerForSingleValueEvent for one single object
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_ID)) {
                            String req_type = dataSnapshot.child(user_ID).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mCurrentState = "req_resived";
                                mProfileSendReqBtn.setBackgroundResource(R.color.Acc_Btn);
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                mCurrentState = "req_sentt";
                                mProfileSendReqBtn.setBackgroundResource(R.color.button);
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);
                            }
                            mProfileProgress.dismiss();
                        } else {
                            //already friends
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_ID)) {
                                        mCurrentState = "friends";
                                        mProfileSendReqBtn.setBackgroundResource(R.color.Acc_Btn);
                                        mProfileSendReqBtn.setText("Unfriend this Person");
                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);
                                    }
                                    mProfileProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                //  - ---------------------Not Friend State --------------------

                if (mCurrentState.equals("not_friends")) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_ID).child("request_type").setValue("sent").
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_ID).child("name").setValue(dataSnapshot.child("name").getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        mFriendReqDatabase.child(user_ID).child(mCurrentUser.getUid()).child("request_type").setValue("received").
                                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                mFriendReqDatabase.child(user_ID).child(mCurrentUser.getUid()).child("name").setValue(dataSnapshot.child("name").getValue().toString());
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                        HashMap<String, String> notifiactions = new HashMap<>();
                                                        notifiactions.put("from", mCurrentUser.getUid());
                                                        notifiactions.put("type", "request");

                                                        mNotificationsDatabase.child(user_ID).push().setValue(notifiactions).
                                                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mCurrentState = "req_sent";
                                                                        mProfileSendReqBtn.setBackgroundResource(R.color.button);
                                                                        mProfileSendReqBtn.setText("Cancel Friend Request");
                                                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                                        mProfileDeclineReqBtn.setEnabled(false);

                                                                    }
                                                                });
//                                                        Toast.makeText(ProfileActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed Sending Request.", Toast.LENGTH_SHORT).show();
                                    }
                                    mProfileSendReqBtn.setEnabled(true);

                                }
                            });
                }

                //  - ---------------------Cancel Request State --------------------
                if (mCurrentState.equals("req_sent")) {
                    Toast.makeText(ProfileActivity.this, "lollll", Toast.LENGTH_SHORT).show();
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_ID).removeValue().
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(user_ID).child(mCurrentUser.getUid()).removeValue().
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrentState = "not_friends";
                                                    mProfileSendReqBtn.setBackgroundResource(R.color.colorAccent);
                                                    mProfileSendReqBtn.setText("Send Friend Request");
                                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineReqBtn.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }

                if (mCurrentState.equals("req_resived")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_ID).setValue(currentDate).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendDatabase.child(user_ID).child(mCurrentUser.getUid()).setValue(currentDate).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_ID).removeValue().
                                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendReqDatabase.child(user_ID).child(mCurrentUser.getUid()).removeValue().
                                                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    mProfileSendReqBtn.setEnabled(true);
                                                                                    mCurrentState = "friends";
                                                                                    mProfileSendReqBtn.setBackgroundResource(R.color.Acc_Btn);
                                                                                    mProfileSendReqBtn.setText("Unfriend this Person");
                                                                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                                                    mProfileDeclineReqBtn.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                }
                if (mCurrentState.equals("friends")) {
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_ID).removeValue().
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(user_ID).child(mCurrentUser.getUid()).removeValue().
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrentState = "not_friends";
                                                    mProfileSendReqBtn.setBackgroundResource(R.color.colorAccent);
                                                    mProfileSendReqBtn.setText("Send Friend Request");
                                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineReqBtn.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }
            }

        });


        mProfileDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_ID).removeValue().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase.child(user_ID).child(mCurrentUser.getUid()).removeValue().
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProfileSendReqBtn.setEnabled(true);
                                                mCurrentState = "not_friends";
                                                mProfileSendReqBtn.setBackgroundResource(R.color.colorAccent);
                                                mProfileSendReqBtn.setText("Send Friend Request");
                                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                mProfileDeclineReqBtn.setEnabled(false);
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserRef.child("online").setValue("true");
    }

}


