package com.example.buckit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signin;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signin = (Button) findViewById(R.id.signin);
        register = (Button) findViewById(R.id.register);

        signin.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.signin) {
            Toast.makeText(this, "Signed in successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.register) {
            Toast.makeText(this, "Redirecting to registration.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
