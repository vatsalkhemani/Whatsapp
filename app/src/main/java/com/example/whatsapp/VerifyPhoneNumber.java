package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class VerifyPhoneNumber extends AppCompatActivity {

    private String mVerification;
    private FirebaseAuth mAuth;
    private EditText mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);
        mAuth=FirebaseAuth.getInstance();
        mCode=findViewById(R.id.code);

        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");
        Toast.makeText(getApplicationContext(),"mobile is" + mobile,Toast.LENGTH_SHORT).show();
        sendVerificationCode(mobile);

        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=mCode.getText().toString();
                verifyCode(code);
            }
        });
    }

    public void verifyCode(String code){
        try{
            PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerification,code);
            signInWithPhone(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
    private void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNumber.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {







                            Intent intent=new Intent(VerifyPhoneNumber.this,ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


    public void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,             // Activity (for callback binding)
                mCallbacks);


    }



    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks()

    {
        @Override
        public void onCodeSent (String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            mVerification = s;
            Toast.makeText(getApplicationContext(), "Code sent", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onVerificationCompleted (PhoneAuthCredential phoneAuthCredential){
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                mCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed (FirebaseException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    };

}