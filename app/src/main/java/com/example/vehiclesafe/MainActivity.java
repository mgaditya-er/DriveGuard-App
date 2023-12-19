package com.example.vehiclesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    EditText textEditUsername, textEditPassword;

    String username, password;

    Button login;

    CardView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textEditUsername = findViewById(R.id.usernameTextEdit);
        textEditPassword = findViewById(R.id.passwordTextEdit);
        login = findViewById(R.id.logInBtn);
        signup = findViewById(R.id.signUp);

        login.setOnClickListener(new View.OnClickListener() {
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
                if(!username.equals("") && !password.equals("")){
                    Toast.makeText(MainActivity.this, "Logged in successfully !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Activity_HomePage.class);

                    startActivity(intent);
                    finish();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity_Signup.class);
                startActivity(intent);
                finish();
            }
        });

    }
}