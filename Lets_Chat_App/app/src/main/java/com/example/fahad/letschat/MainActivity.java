package com.example.fahad.letschat;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
    private DatabaseReference databaseReference,ShowAllUsers,OneUser;
    private RecyclerView rvAllUsers;
    private String currentname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=(Toolbar)findViewById(R.id.main_menu_bar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");

        //myName=(TextView)findViewById(R.id.main_id);
        //usr_picture=(ImageView)findViewById(R.id.usr_id);

        rvAllUsers= (RecyclerView) findViewById(R.id.rvAllUsers);
        rvAllUsers.setHasFixedSize(true);
        rvAllUsers.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        else
        {
            final String uid=currentUser.getUid();

//            OneUser=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//            OneUser.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    currentname=dataSnapshot.child("name").getValue().toString();
////                    Toast.makeText(MainActivity.this, currentname,
////                            Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            ShowAllUsers=FirebaseDatabase.getInstance().getReference().child("Users");
//            ShowAllUsers.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Toast.makeText(MainActivity.this,snapshot.getKey(),
//                                Toast.LENGTH_SHORT).show();
//
//                        String namess=dataSnapshot.child(snapshot.getKey()).child("name").getValue().toString();
//
//                        if(namess.equals(currentname))
//                        {
//                            dataSnapshot.removeValue();
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            FirebaseRecyclerAdapter<AllUsers,UserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllUsers, UserViewHolder>(
                    AllUsers.class,R.layout.activity_single_user,UserViewHolder.class,ShowAllUsers) {

                @Override
                protected void populateViewHolder(final UserViewHolder viewHolder, AllUsers model, int position) {

                    final String usr_id=getRef(position).getKey();
                    ShowAllUsers.child(usr_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name=dataSnapshot.child("name").getValue().toString();
                            String image=dataSnapshot.child("thumb_image").getValue().toString();
                            viewHolder.SetName(name);
                            viewHolder.SetImage(image,getApplicationContext());
                            viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatintent=new Intent(getApplicationContext(),Chat_activity.class);
                                    chatintent.putExtra("user_id",usr_id);
                                    startActivity(chatintent);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }


            };
            rvAllUsers.setAdapter(firebaseRecyclerAdapter);


//            String uid=currentUser.getUid();
//            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String name=dataSnapshot.child("name").getValue().toString();
//                    String image=dataSnapshot.child("image").getValue().toString();
//                    String Welcometxt="Welcome "+ name;
//                    //myName.setText(Welcometxt);
//                    //Picasso.with(MainActivity.this).load(image).into(usr_picture);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        }
//        getSupportActionBar().setTitle("LetsChat");



        //FirebaseUser user = mAuth.getInstance().getCurrentUser();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public UserViewHolder(View itemView) {
            super(itemView);
            myView=itemView;
        }

        public void SetName(String Name)
        {
            TextView dispName=(TextView)myView.findViewById(R.id.specific_userNames);
            dispName.setText(Name);
        }

        public void SetImage(String Image, Context context)
        {
            ImageView usr_pics=(ImageView)myView.findViewById(R.id.profiles_pictures);
            Picasso.with(context).load(Image).placeholder(R.drawable.usp).into(usr_pics);
        }
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
