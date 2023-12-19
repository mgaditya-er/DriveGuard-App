package com.example.vehiclesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vehiclesafe.databinding.ActivitySignupBinding;

public class Activity_Signup extends AppCompatActivity {

    ActivitySignupBinding binding;

    EditText textEditUsername, textEditPassword, textEditPhoneNo;

    String username, password, phone;

    Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textEditUsername = findViewById(R.id.usernameTextEdit);
        textEditPassword = findViewById(R.id.passwordTextEdit);
        textEditPhoneNo = findViewById(R.id.phoneNoTextEdit);
        signup = findViewById(R.id.signUpBtn);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = textEditUsername.getText().toString();
                if(username.length() == 0) {
                    textEditUsername.setError("Username is required");
                }
                password = textEditPassword.getText().toString();
                if(password.length() == 0) {
                    textEditPassword.setError("Password is required");
                }
                phone = textEditPhoneNo.getText().toString();
                if(phone.length() == 0) {
                    textEditPhoneNo.setError("Password is required");
                }
                if(!username.equals("") && !password.equals("") && !phone.equals("")) {
                    Toast.makeText(Activity_Signup.this, "Account Created successfully !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_Signup.this, Activity_HomePage.class);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
