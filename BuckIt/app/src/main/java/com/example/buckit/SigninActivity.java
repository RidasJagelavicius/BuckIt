package com.example.buckit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signin;
    private Button register;
    private EditText signinpass;
    private EditText signinuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signin = (Button) findViewById(R.id.signin);
        register = (Button) findViewById(R.id.register);
        signinpass = findViewById(R.id.signinpass);
        signinuser = findViewById(R.id.signinuser);

        signin.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.signin) {
            String innerUser = signinuser.getText().toString();
            String innerPass = signinpass.getText().toString();
            if (innerUser.length() == 0) {
                Toast.makeText(this, "Username not found, please try again", Toast.LENGTH_SHORT).show();
            } else if (innerPass.length() == 0) {
                Toast.makeText(this, "Incorrect password, please try again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
