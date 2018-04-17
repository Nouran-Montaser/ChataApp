package com.example.lenovo.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * Created by Lenovo on 2/16/2018.
 */

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Conversation> Convdetails;

    public ChatAdapter(Context context, ArrayList<Conversation> Convdetails) {
        this.context = context;
        this.Convdetails = Convdetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nameTextView.setText(Convdetails.get(position).getUser());
        holder.messageTextView.setText(Convdetails.get(position).getMessage());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context , ChatActivity.class);
                chatIntent.putExtra("user_ID", Convdetails.get(position).getFrom());
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Convdetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View item;
        TextView nameTextView;
        TextView messageTextView;

        public ViewHolder(View view) {
            super(view);
            item = view;
            nameTextView = view.findViewById(R.id.message_user_name);
            messageTextView = view.findViewById(R.id.single_user_lastMessage);
        }
    }
}




