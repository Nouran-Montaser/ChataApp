package com.example.lenovo.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

public class LogInActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mDisplayEmail;
    private TextInputLayout mDisplayPass;
    private Button mLogInButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayEmail = findViewById(R.id.log_email);
        mDisplayPass = findViewById(R.id.log_pass);
        mLogInButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        mRegProgress = new ProgressDialog(this);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_email = mDisplayEmail.getEditText().getText().toString();
                String display_pass = mDisplayPass.getEditText().getText().toString();
                if (!TextUtils.isEmpty(display_email) || !TextUtils.isEmpty(display_pass)) {
                    mRegProgress.setTitle("Logging In");
                    mRegProgress.setMessage("Please wait while we check your credentials!");
                    mRegProgress.setCanceledOnTouchOutside(false);//prevenr user from touch on screen
                    mRegProgress.show();

                    login_user(display_email, display_pass);
                }

            }
        });


    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mRegProgress.dismiss();

                    String token_ID = FirebaseInstanceId.getInstance().getToken();
                    String User_ID = mAuth.getCurrentUser().getUid();

                    databaseReference.child(User_ID).child("token_id").setValue(token_ID).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent main = new Intent(LogInActivity.this, MainActivity.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(main);
                            finish();

                        }
                    });

                } else {
                    mRegProgress.hide();
                    Toast.makeText(LogInActivity.this, "Cannot Sign in. Please check your email and password then try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference.child(mAuth.getCurrentUser().getUid());
            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
