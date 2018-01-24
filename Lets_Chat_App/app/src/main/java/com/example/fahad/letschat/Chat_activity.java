package com.example.fahad.letschat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat_activity extends AppCompatActivity {

    public String chatusr;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        chatusr=getIntent().getStringExtra("user_id");
        Toast.makeText(Chat_activity.this, chatusr,
                Toast.LENGTH_SHORT).show();
        toolbar=(Toolbar)findViewById(R.id.chat_bar);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(chatusr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
