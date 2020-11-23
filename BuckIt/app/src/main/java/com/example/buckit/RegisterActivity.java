package com.example.buckit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button createAccount;
    private EditText regFull;
    private EditText regUser;
    private EditText regEmail;
    private EditText regPass1;
    private EditText regPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);

        regFull = findViewById(R.id.regFull);
        regFull.setOnClickListener(this);
        regUser = findViewById(R.id.regFull);
        regUser.setOnClickListener(this);
        regEmail = findViewById(R.id.regFull);
        regEmail.setOnClickListener(this);
        regPass1 = findViewById(R.id.regFull);
        regPass1.setOnClickListener(this);
        regPass2 = findViewById(R.id.regFull);
        regPass2.setOnClickListener(this);
    }


    public void onClick(View v) {
        if (v.getId() == R.id.createAccount) {
            String fulltxt = regFull.getText().toString();
            String usertxt = regUser.getText().toString();
            String emailtxt = regEmail.getText().toString();
            String pass1txt = regPass1.getText().toString();
            String pass2txt = regPass2.getText().toString();

            if (fulltxt.length() == 0 || usertxt.length() == 0 || emailtxt.length() == 0 || pass1txt.length() == 0 || pass2txt.length() == 0) {
                Toast.makeText(this, "Unable to create account, please populate all fields", Toast.LENGTH_SHORT).show();
            } else if (!pass1txt.equals(pass2txt)) {
                Toast.makeText(this, "Unable to create account, passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Created an account, welcome to Buckit!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}