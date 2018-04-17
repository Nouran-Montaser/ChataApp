package com.example.lenovo.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mRef;
    private TextView mNameTextview;
    private TextView mStatusTextview;
    private CircleImageView mImageview;
    private Button mStatusBtn;
    private Button mChangeIMGBtn;
    private static final int GALLERY_PIC=1;
    //private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mNameTextview = findViewById(R.id.setting_name);
        mStatusTextview = findViewById(R.id.setting_status);
        mImageview = findViewById(R.id.profile_image);
        mStatusBtn = findViewById(R.id.changeStatus_Button);
        mChangeIMGBtn = findViewById(R.id.changeImage_Button);

        //mStorageRef = FirebaseStorage.get

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid = mCurrentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);
        mRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                mNameTextview.setText(name);
                mStatusTextview.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = mStatusTextview.getText().toString();
                Intent statusIntent = new Intent(SettingActivity.this ,StatusActivity.class);
                statusIntent.putExtra("status_value",status);
                startActivity(statusIntent);
            }
        });


        mChangeIMGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(galleryIntent , "Select Image" ),GALLERY_PIC);
              //  CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(SettingActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PIC && resultCode == RESULT_OK)
        {
            Uri imgUri = data.getData();
//            Toast.makeText(SettingActivity.this , imgUri , Toast.LENGTH_LONG).show();
//            CropImage.activity(imgUri).setAspectRatio(1,1).start(this);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mRef.child("online").setValue("true");
    }



}

