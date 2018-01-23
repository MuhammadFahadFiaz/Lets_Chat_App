package com.example.fahad.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout Username,Email,Password;
    private Button register;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        Username=(TextInputLayout)findViewById(R.id.UserName);
        Email=(TextInputLayout) findViewById(R.id.Register_email);
        Password=(TextInputLayout) findViewById(R.id.Register_password);
        register=(Button)findViewById(R.id.Register_main);
        toolbar=(Toolbar)findViewById(R.id.Register_menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username_firebase=Username.getEditText().getText().toString();
                String email_firebase=Email.getEditText().getText().toString();
                String password_firebase=Password.getEditText().getText().toString();
                register_User_Firebase(Username_firebase,email_firebase,password_firebase);
            }
        });
    }

    private void register_User_Firebase(String username,String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent startIntent=new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(startIntent);
                            finish();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }

                    private void updateUI(FirebaseUser user) {
                    }
                });

    }
}
