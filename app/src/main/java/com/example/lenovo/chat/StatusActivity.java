package com.example.lenovo.chat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mStatusBtn;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mStatusProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid = mCurrentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(current_Uid);

        mToolbar = findViewById(R.id.status_page_toobar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusValue = getIntent().getStringExtra("status_value");

        mStatus = findViewById(R.id.status_input);
        mStatusBtn = findViewById(R.id.status_saveBtn);

        mStatus.getEditText().setText(statusValue);

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatusProgress = new ProgressDialog(StatusActivity.this);
                mStatusProgress.setTitle("Saving Changes");
                mStatusProgress.setMessage("Please wait while we save the changes");
                mStatusProgress.setCanceledOnTouchOutside(false);//prevenr user from touch on screen
                mStatusProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mDatabaseRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mStatusProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext() , "There was some error in saving Changes" ,Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }


    
    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseRef.child("online").setValue("true");
    }



}
