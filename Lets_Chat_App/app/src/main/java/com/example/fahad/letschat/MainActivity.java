package com.example.fahad.letschat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private static int SPLASH_OUT_TIME=3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=(Toolbar)findViewById(R.id.main_menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("LetsChat");

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
