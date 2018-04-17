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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

    private Context context;
    private ArrayList<Users> details;
    private ArrayList<Friends> frienddetails;
    private String s;
    private DatabaseReference mUserDatabase;


    public adapter(Context context, ArrayList<Users> details, String s) {
        this.context = context;
        this.details = details;
        this.s = s;
    }

    public adapter(Context context, String s, ArrayList<Friends> frienddetails) {
        this.context = context;
        this.frienddetails = frienddetails;
        this.s = s;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        if (!s.equals("friends")) {
            holder.nameTextView.setText(details.get(position).getName());
            holder.statusTextView.setText(details.get(position).getStatus());
//        holder.circleImageView.setImageDrawable(details.get(position).getImage());
            Picasso.with(context).load(details.get(position).getImage()).into(holder.circleImageView);
//        Picasso.with(context).load(details.get(position).getImage()).placeholder(R.drawable.defaultt).into(holder.circleImageView);
//        Toast.makeText(context , details.get(position).getId(),Toast.LENGTH_LONG).show();
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_ID", details.get(position).getId());
                    context.startActivity(profileIntent);
                }
            });
        } else {
            final Intent chatIntent = new Intent(context, ChatActivity.class);
            holder.statusTextView.setText(frienddetails.get(position).getDate());
            String ID = frienddetails.get(position).getId();
    //        Toast.makeText(context, ID, Toast.LENGTH_SHORT).show();
            mUserDatabase.child(ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userOnline = dataSnapshot.child("online").getValue().toString();
                    if (dataSnapshot.hasChild("online")) {
                        holder.nameTextView.setText(userName);
                        chatIntent.putExtra("user_Name", userName);
                        if (userOnline.equals("true"))
                            holder.onlineIcon.setVisibility(View.VISIBLE);
                        else
                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharSequence options[] = new CharSequence[]{"Open Profile" , "Send Message"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 0)
                            {
                                Intent profileIntent = new Intent(context, ProfileActivity.class);
                                profileIntent.putExtra("user_ID", frienddetails.get(position).getId());
                                context.startActivity(profileIntent);
                            }
                            if(i == 1)
                            {
//                                Intent chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra("user_ID", frienddetails.get(position).getId());
//                                chatIntent.putExtra("user_Name", userName);
                                context.startActivity(chatIntent);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (s.equals("friends"))
            return frienddetails.size();
        else
            return details.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View item;
        TextView nameTextView;
        TextView statusTextView;
        // CircleImageView circleImageView;
        ImageView circleImageView;
        ImageView onlineIcon;

        public ViewHolder(View view) {
            super(view);
            item = view;
            nameTextView = view.findViewById(R.id.single_user_name);
            statusTextView = view.findViewById(R.id.single_user_status);
            circleImageView = view.findViewById(R.id.single_user_img);
            onlineIcon = view.findViewById(R.id.single_user_onlineIcon);
        }
    }
}



