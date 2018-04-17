package com.example.lenovo.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Requests> Reqdetails;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendDatabase;

    public RequestAdapter(Context context, ArrayList<Requests> Reqdetails) {
        this.context = context;
        this.Reqdetails = Reqdetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mReqtxt.setVisibility(View.INVISIBLE);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference("FriendReq");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference("Friends");

        // mFriendDatabase = FirebaseDatabase.getInstance().getReference("Friends");
        holder.nameTextView.setText(Reqdetails.get(position).getName());
        if ((Reqdetails.get(position).getType()).equals("received")) {
            holder.mAcceptBtn.setVisibility(View.VISIBLE);
            holder.mDeclineBtn.setVisibility(View.VISIBLE);
            holder.mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(Reqdetails.get(position).getId()).removeValue().
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(Reqdetails.get(position).getId()).child(mCurrentUser.getUid()).removeValue().
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    holder.mAcceptBtn.setVisibility(View.INVISIBLE);
                                                    holder.mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    holder.mReqtxt.setVisibility(View.VISIBLE);
                                                    holder.mReqtxt.setText("Canceled");
                                                }
                                            });
                                }
                            });

                }
            });


            holder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(Reqdetails.get(position).getId()).setValue(currentDate).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(Reqdetails.get(position).getId()).child(mCurrentUser.getUid()).setValue(currentDate).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(Reqdetails.get(position).getId()).removeValue().
                                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendReqDatabase.child(Reqdetails.get(position).getId()).child(mCurrentUser.getUid()).removeValue().
                                                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    holder.mAcceptBtn.setVisibility(View.INVISIBLE);
                                                                                    holder.mDeclineBtn.setVisibility(View.INVISIBLE);
                                                                                    holder.mReqtxt.setVisibility(View.VISIBLE);
                                                                                    holder.mReqtxt.setText("You are now friends");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                }
            });


        } else {
            holder.mDeclineBtn.setVisibility(View.INVISIBLE);
            holder.mAcceptBtn.setVisibility(View.VISIBLE);
            holder.mAcceptBtn.setText("Cancle");
            holder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(Reqdetails.get(position).getId()).removeValue().
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(Reqdetails.get(position).getId()).child(mCurrentUser.getUid()).removeValue().
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    holder.mAcceptBtn.setVisibility(View.INVISIBLE);
                                                    holder.mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    holder.mReqtxt.setVisibility(View.VISIBLE);
                                                    holder.mReqtxt.setText("Cancled");

                                                }
                                            });
                                }
                            });
                }


                });

        }
        holder.nameTextView.setText(Reqdetails.get(position).getName());
        Toast.makeText(context, "req", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return Reqdetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View item;
        TextView nameTextView;
        Button mAcceptBtn;
        Button mDeclineBtn;
        TextView mReqtxt;

        public ViewHolder(View view) {
            super(view);
            item = view;
            nameTextView = view.findViewById(R.id.user_request);
            mAcceptBtn = view.findViewById(R.id.single_accept);
            mDeclineBtn = view.findViewById(R.id.single_decline);
            mReqtxt = view.findViewById(R.id.RequestText);
        }
    }
}




