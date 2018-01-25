package com.example.fahad.letschat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.Manifest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout Username,Email,Password;
    private Button register,upload_image;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private static final int profile_picture=1;
    //storing pictures
    private StorageReference mStorageRef,thumb_upload;
    byte[] compress_bitmap;

    private FirebaseAuth mAuth;
    Uri resultUri;
    String random_name;
    String download_url;
    String Thums_download_url;
    String Picturs_checks="";
    int wait=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        Username=(TextInputLayout)findViewById(R.id.UserName);
        Email=(TextInputLayout) findViewById(R.id.Register_email);
        Password=(TextInputLayout) findViewById(R.id.Register_password);
        register=(Button)findViewById(R.id.Register_main);
        upload_image=(Button)findViewById(R.id.Upload_image);
        toolbar=(Toolbar)findViewById(R.id.Register_menu_bar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final Context ctx = getApplicationContext();


        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upload_image_gallery=new Intent();
                upload_image_gallery.setType("image/*");
                upload_image_gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(upload_image_gallery,"Select Image"),profile_picture);


//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(RegisterActivity.this);
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Username_firebase=Username.getEditText().getText().toString();
                String email_firebase=Email.getEditText().getText().toString();
                String password_firebase=Password.getEditText().getText().toString();
                if (TextUtils.isEmpty(Username_firebase)) {
                    Toast.makeText(getApplicationContext(), "UserName Cannot Be Blank!", Toast.LENGTH_SHORT).show();
                    Username.getEditText().requestFocus();
                    return;
                }
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
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(Picturs_checks)) {
                    Toast.makeText(getApplicationContext(), "Please Select Some Image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Picturs_checks.equals("alpha")) {
                    Toast.makeText(getApplicationContext(), "Please wait while we upload Image.Then Press Register Button!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!checkInternetConenction())
                {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Check_Email_Already_Exist_Or_Not(Username_firebase,email_firebase,password_firebase);

            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1222);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == profile_picture && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                File thmb=new File(resultUri.getPath());

                //Toast.makeText(RegisterActivity.this, resultUri.toString(),Toast.LENGTH_SHORT).show();
                random_name= UUID.randomUUID().toString();
                if (!checkInternetConenction())
                {
                    return;
                }
                Picturs_checks="alpha";
                mStorageRef= FirebaseStorage.getInstance().getReference();
                try {
                    Bitmap compress_image = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(75).compressToBitmap(thmb);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compress_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    compress_bitmap = baos.toByteArray();
                    thumb_upload=mStorageRef.child("profile_images").child("thumbs").child(random_name+".jpg");



                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference filepath = mStorageRef.child("profile_images").child(random_name+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            download_url=task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_upload.putBytes(compress_bitmap);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task_thumb) {
                                    Thums_download_url=task_thumb.getResult().getDownloadUrl().toString();

                                    if (task_thumb.isSuccessful()) {
                                        Picturs_checks="bETA";

                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this, "failed Thumbs.",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }

                            });
                            Toast.makeText(RegisterActivity.this, "Image Uploaded Sucessfully.",
                                    Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
        ;
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
    private void register_User_Firebase(final String username, final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getInstance().getCurrentUser();
                                String uid=user.getUid();
                                int num=1;

                                databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                HashMap<String,String> saveUser=new HashMap<>();
                                saveUser.put("name",username);
                                saveUser.put("status","Hey there I'm using LetsChat");
                                saveUser.put("image",download_url);
                                saveUser.put("thumb_image", Thums_download_url);
                                databaseReference.setValue(saveUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent startIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(startIntent);
                                            finish();

                                        }
                                    }
                                });

                        } else {
                            // If sign in fails, display a message to the user.

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Please connect to Internet And Try Again.",
                                    Toast.LENGTH_SHORT).show();


//                            updateUI(null);
                        }

                        // ...
                    }

                    private void updateUI(FirebaseUser user) {
                    }
                });

    }
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Please Connect To Internet And Try Again", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
