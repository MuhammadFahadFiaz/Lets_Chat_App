package com.example.fahad.letschat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button register,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        register=(Button)findViewById(R.id.Register_me);
        login=(Button)findViewById(R.id.Login_me);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(registerIntent);

            }
        });
    }
}
