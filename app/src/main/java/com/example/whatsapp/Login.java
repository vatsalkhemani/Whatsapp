package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNumber = findViewById(R.id.phone);
        mAuth=FirebaseAuth.getInstance();

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile=mPhoneNumber.getText().toString();
              Intent intent=new Intent(Login.this,VerifyPhoneNumber.class);
              intent.putExtra("mobile",mobile);
              startActivity(intent);
            }
        });
    }






}
