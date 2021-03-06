package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText mPhoneNumber;
    EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNumber = findViewById(R.id.phone);
        mAuth=FirebaseAuth.getInstance();
        mName=findViewById(R.id.name);

        //To keep the user Logged In
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(Login.this, ProfileActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile=mPhoneNumber.getText().toString();
                String name=mName.getText().toString();
              Intent intent=new Intent(Login.this,VerifyPhoneNumber.class);
              Bundle extras =new Bundle();
              extras.putString("mobile",mobile);
              extras.putString("displayname",name);
              intent.putExtras(extras);
              startActivity(intent);
            }
        });
    }






}
