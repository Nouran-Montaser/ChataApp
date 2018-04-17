package com.example.lenovo.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mDisplayEmail;
    private TextInputLayout mDisplayPass;
    private Button mCreateButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference mUserRef;
    private DatabaseReference mDatabaseRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mToolbar = findViewById(R.id.Register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mDisplayName = findViewById(R.id.reg_name);
        mDisplayEmail = findViewById(R.id.reg_email);
        mDisplayPass = findViewById(R.id.reg_pass);
        mCreateButton = findViewById(R.id.createAccount_button);
        mAuth = FirebaseAuth.getInstance();

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getEditText().getText().toString();
                String display_email = mDisplayEmail.getEditText().getText().toString();
                String display_pass = mDisplayPass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(display_email) || !TextUtils.isEmpty(display_pass))
                {
                    mRegProgress.setTitle("registering User");
                    mRegProgress.setMessage("Please wait while we create your account!");
                    mRegProgress.setCanceledOnTouchOutside(false);//prevenr user from touch on screen
                    mRegProgress.show();

                    regster_user(display_name, display_email, display_pass);
                }


            }
        });


    }

    private void regster_user(final String name, final String email, String password) {
        //Toast.makeText(RegisterActivity.this, name + "    " + email + "    " + password, Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String token_ID = FirebaseInstanceId.getInstance().getToken();
                    String User_ID = mAuth.getCurrentUser().getUid();

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",name);
                    userMap.put("status","Hi there, I'm using chat.");
                    userMap.put("thumb_image","default");
                    userMap.put("token_id",token_ID);
                    mDatabaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mRegProgress.dismiss();
                                Intent main = new Intent(RegisterActivity.this, MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//3shan lma ydos back msh yro7 l el startActivity tany
                                startActivity(main);
                                finish();
                            }
                        }
                    });
                } else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot Sign up. Please check the form then try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userID=mAuth.getCurrentUser().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}














