package com.example.lenovo.chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private String mCurrentUser;

    public MessageAdapter(List<Messages> mMessagesList , String mCurrentUser ) {
        this.mCurrentUser=mCurrentUser;
        this.mMessagesList = mMessagesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if (!mAuth.getCurrentUser().equals(null)) {
//            String current_user_id = mAuth.getCurrentUser().getUid();
            Messages c = mMessagesList.get(position);

            String from_user = c.getFrom();
            if (from_user.equals(mCurrentUser)) {
                holder.relativeLayout.setGravity(Gravity.RIGHT);
                holder.imageView.setVisibility(View.GONE);
                holder.messageTextView.setBackgroundResource(R.drawable.message_text_background);
                holder.messageTextView.setTextColor(Color.WHITE);
            } else {
                holder.relativeLayout.setGravity(Gravity.LEFT);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.messageTextView.setBackgroundResource(R.drawable.message_text);
                holder.messageTextView.setTextColor(Color.BLACK);
            }
            holder.messageTextView.setText(c.getMessage());
        //}

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View item;
        TextView messageTextView;
        RelativeLayout relativeLayout;
        CircleImageView imageView;

        public ViewHolder(View view) {
            super(view);
            item = view;
            imageView = view.findViewById(R.id.single_message_image);
            relativeLayout = view.findViewById(R.id.single_layout);
            messageTextView = view.findViewById(R.id.single_message_text);
        }
    }
}




