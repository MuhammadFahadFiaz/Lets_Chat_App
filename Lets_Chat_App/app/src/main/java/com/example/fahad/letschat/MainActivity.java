package com.example.fahad.letschat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private static int SPLASH_OUT_TIME=3000;
    private DatabaseReference databaseReference;
    private TextView myName;
    private ImageView usr_picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=(Toolbar)findViewById(R.id.main_menu_bar);
        setSupportActionBar(toolbar);
        myName=(TextView)findViewById(R.id.main_id);
        usr_picture=(ImageView)findViewById(R.id.usr_id);


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Intent myIntent=new Intent(MainActivity.this,Register_Activity.class);
//                startActivity(myIntent);
//                finish();
//
//            }
//
//        },SPLASH_OUT_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        else
        {
            String uid=currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String image=dataSnapshot.child("image").getValue().toString();
                    String Welcometxt="Welcome "+ name;
                    myName.setText(Welcometxt);
                    Picasso.with(MainActivity.this).load(image).into(usr_picture);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        getSupportActionBar().setTitle("LetsChat");



        //FirebaseUser user = mAuth.getInstance().getCurrentUser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logout_btn){
            FirebaseAuth.getInstance().signOut();
            Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        return true;
    }
}
