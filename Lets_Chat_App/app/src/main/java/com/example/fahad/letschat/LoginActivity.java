package com.example.fahad.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout Email,Password;
    private Button login;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        Email=(TextInputLayout) findViewById(R.id.login_email);
        Password=(TextInputLayout) findViewById(R.id.login_password);
        login=(Button)findViewById(R.id.login_main);
        toolbar=(Toolbar)findViewById(R.id.Login_menu_bar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_firebase=Email.getEditText().getText().toString();
                String password_firebase=Password.getEditText().getText().toString();
                if (TextUtils.isEmpty(email_firebase)) {
                    Email.getEditText().requestFocus();
                    Toast.makeText(getApplicationContext(), "Email Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_firebase)) {
                    Password.getEditText().requestFocus();
                    Toast.makeText(getApplicationContext(), "Password Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password_firebase.length() < 6) {
                    Password.getEditText().requestFocus();
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                login_Check_Firebase(email_firebase,password_firebase);
            }
        });

    }

    public void login_Check_Firebase(final String email,final String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent startIntent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(startIntent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Entered Email Or Password Incorrect!!!Try Again.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
