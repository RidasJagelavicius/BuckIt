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

public class PostActivity2 extends AppCompatActivity implements View.OnClickListener {

//    private TextView username3;
//    private Button sendFriendRequestButton;
//    private String username;

    private Button friendButton;
    private String username;
    private boolean isAdded;
    private ArrayList<String> added_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_addusers2);
        friendButton = findViewById(R.id.friend_button);
        friendButton.setOnClickListener(this);
        TextView username2 = findViewById(R.id.username2);
        username2.setText(username);
        friendButton.setText(username);
        Bundle b = this.getIntent().getExtras();
        added_users = b.getStringArrayList("added_users");

        isAdded = false;
        for (int i = 0; i < added_users.size(); i++) {
            if (added_users.get(i).equals(username)) {
                isAdded = true;
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.friend_button) { // 3a
            if (!isAdded) {
                Intent intent = new Intent(this, PostActivity3a.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else { // 3b
                added_users.add(username);
                Intent intent = new Intent(this, PostActivity3b.class);
                intent.putExtra("username", username);
                Toast.makeText(this, "Friend request accepted.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }
}
