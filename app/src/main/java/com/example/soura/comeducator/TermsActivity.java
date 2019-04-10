package com.example.soura.comeducator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;

public class TermsActivity extends AppCompatActivity {

    TextView termsView;
    String text;
    String user_id;
    CheckBox acceptcheck;
    Button submitbutton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        termsView = (TextView)findViewById(R.id.tandc);
        acceptcheck = (CheckBox)findViewById(R.id.checkBox);
        submitbutton = (Button)findViewById(R.id.submit);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        try
        {
            InputStream is = getAssets().open("tandc.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
            termsView.setText(text);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!acceptcheck.isChecked())
                {
                    Snackbar.make(view, "Please Accept the Terms and Condition", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                else
                {
                    mDatabase.child("terms").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent login=new Intent(TermsActivity.this,HomePageActivity.class);
                                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(login);
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }
}
