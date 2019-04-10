package com.example.soura.comeducator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DecisionActivity extends AppCompatActivity {

    GifImageView gifImageView;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);

        gifImageView = (GifImageView) findViewById(R.id.gifImageView);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);


        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(mUserRef.child(mCurrentUserId)!=null) {
                    Boolean tc = (Boolean) dataSnapshot.child("terms").getValue();

                    if (tc.equals(false)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent account = new Intent(DecisionActivity.this, TermsActivity.class);
                                account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(account);
                                finish();
                            }
                        }, 2000);
                    } else if (tc.equals(true)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent account = new Intent(DecisionActivity.this, HomePageActivity.class);
                                account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(account);
                                finish();
                            }
                        }, 5000);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}