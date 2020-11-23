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

public class PostActivity3a extends AppCompatActivity implements View.OnClickListener {
    private TextView username3;
    private Button sendFriendRequestButton;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers3);
        username = getIntent().getStringExtra("username");
        username3 = findViewById(R.id.username3);
        username3.setText(username);
        sendFriendRequestButton = findViewById(R.id.send_friend_request_button);
        sendFriendRequestButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_friend_request_button) {
            Intent intent = new Intent(this, PostActivity3b.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }
}
