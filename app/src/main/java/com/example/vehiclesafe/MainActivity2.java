package com.example.vehiclesafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {

    EditText phone,otp;
    Button btnsendotp,btnverifyotp;
    String verficationId;
    ProgressBar bar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainotp);

        phone = findViewById(R.id.editTextPhoneNumber);
        otp = findViewById(R.id.editTextOtp);
        btnsendotp = findViewById(R.id.buttonSendOtp);
        btnverifyotp = findViewById(R.id.buttonVerifyOtp);
        mAuth = FirebaseAuth.getInstance();
        bar = findViewById(R.id.bar);


        btnsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phone.getText().toString()))
                {
                    Toast.makeText(MainActivity2.this, "Enter a valid number", Toast.LENGTH_SHORT).show();
                }
                else {
                    String number  = phone.getText().toString();
                    bar.setVisibility(View.VISIBLE);
                    sendverficationcode(number);
                }
            }
        });

        btnverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(otp.getText().toString()))
                {
                    Toast.makeText(MainActivity2.this, "Wrong Otp", Toast.LENGTH_SHORT).show();
                }
                else {

                    verifycode(otp.getText().toString());
                }

            }
        });
    }
    private void verifycode(String code) {

        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(verficationId,code);
        signinbyCred(cred);

    }
    private void signinbyCred(PhoneAuthCredential cred) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity2.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity2.this,Activity_HomePage.class));
                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if(code!=null)
            {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity2.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.INVISIBLE);

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s,token);
            verficationId = s;
            Toast.makeText(MainActivity2.this, "Code Sent.", Toast.LENGTH_SHORT).show();
            btnverifyotp.setEnabled(true);
            btnsendotp.setEnabled(false);
            phone.setEnabled(false);
            otp.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);

        }
    };
    private void sendverficationcode(String number) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentuser!= null)
        {
            startActivity(new Intent(MainActivity2.this, Activity_HomePage.class));
            finish();
        }

  }
}
