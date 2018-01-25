package com.example.fahad.letschat;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Chat_activity extends AppCompatActivity {

    public String chatusr;
    public String current_id_user;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ImageView sendbtn;
    private EditText sendmsg;
    private RecyclerView view_to_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        chatusr=getIntent().getStringExtra("user_id");
//        Toast.makeText(Chat_activity.this, chatusr,
//                Toast.LENGTH_SHORT).show();
        toolbar=(Toolbar)findViewById(R.id.chat_bar);
        sendbtn=(ImageView)findViewById(R.id.msg_send);
        sendmsg=(EditText) findViewById(R.id.my_msg);
        view_to_msg=(RecyclerView)findViewById(R.id.rvChats);
        mAuth=FirebaseAuth.getInstance();
        current_id_user=mAuth.getCurrentUser().getUid();
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

        databaseReference.child("Chat").child(current_id_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatusr))
                {
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map charUserMap = new HashMap();
                    charUserMap.put("Chat/"+current_id_user+"/"+chatusr,chatAddMap);
                    charUserMap.put("Chat/"+chatusr+"/"+current_id_user,chatAddMap);

                    databaseReference.updateChildren(charUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null)
                            {
                                Log.d("Chat Log",databaseError.getMessage().toString());
                            }

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void sendMessage(){
        String msg=sendmsg.getText().toString();

        if(!TextUtils.isEmpty(msg))
        {
            String currentUserref="messages/"+current_id_user+"/"+chatusr;
            String chatUserref="messages/"+chatusr+"/"+current_id_user;
            DatabaseReference dbref=databaseReference.child("messages").child(current_id_user).child(chatusr).push();
            String pushsp=dbref.getKey();
            Map messageMap=new HashMap();
            messageMap.put("message",msg);
            messageMap.put("seen",false);
            messageMap.put("timeStamp",ServerValue.TIMESTAMP);

            Map userMap=new HashMap();
            userMap.put(currentUserref+"/"+pushsp,messageMap);
            userMap.put(chatUserref+"/"+pushsp,messageMap);
            databaseReference.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    sendmsg.getEditableText().clear();
                    Toast.makeText(Chat_activity.this, "We Have Successfully Recieved Your Message On 0ur Server.",
                            Toast.LENGTH_LONG).show();

//                    Snackbar sb;
//                    sb = Snackbar.make(findViewById(R.id.cvs),"Click exit to exit :)",Snackbar.LENGTH_LONG);
//                    sb.dismiss();
//                    sb.setAction("Exit", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            sendmsg.getEditableText().clear();
//                            sb.dismiss();
//                        }
//                    });

                }
            });

        }


    }
}
