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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout Username,Email,Password;
    private Button register;
    private Toolbar toolbar;
    private ProgressBar progressBar;

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Username_firebase=Username.getEditText().getText().toString();
                String email_firebase=Email.getEditText().getText().toString();
                String password_firebase=Password.getEditText().getText().toString();
                if (TextUtils.isEmpty(Username_firebase)) {
                    Toast.makeText(getApplicationContext(), "UserName Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email_firebase)) {
                    Toast.makeText(getApplicationContext(), "Email Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_firebase)) {
                    Toast.makeText(getApplicationContext(), "Password Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password_firebase.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                Check_Email_Already_Exist_Or_Not(Username_firebase,email_firebase,password_firebase);



            }
        });
    }

    private void Check_Email_Already_Exist_Or_Not(final String username,final String email,final String password){
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if(task.isSuccessful()){
                    ///////// getProviders().size() will return size 1. if email ID is available.
                    if(task.getResult().getProviders().size()==0)
                    {

                        register_User_Firebase(username,email,password);

                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Entered Email Address Already Exist!",
                                Toast.LENGTH_SHORT).show();
                       TextInputLayout mails=(TextInputLayout) findViewById(R.id.Register_email);
                       mails.getEditText().getText().clear();
                       mails.requestFocus();


                    };
                }
            }
        });
    }
    private void register_User_Firebase(String username,String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent startIntent=new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(startIntent);
                            finish();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(RegisterActivity.this, "Please connect to Internet And Try Again.",
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
