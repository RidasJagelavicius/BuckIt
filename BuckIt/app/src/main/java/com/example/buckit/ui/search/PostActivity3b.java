package com.example.buckit.ui.search;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.MyListsActivity;
import com.example.buckit.R;

import java.util.ArrayList;

public class PostActivity3b extends AppCompatActivity implements View.OnClickListener {
    private TextView username4;
    private Button cookingButton;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers4);
        username = getIntent().getStringExtra("username");
        username4 = findViewById(R.id.username4);
        username4.setText(username);
        cookingButton = findViewById(R.id.cooking_button);
        cookingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cooking_button) {
            Intent intent = new Intent(this, PostActivity4.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }
}
