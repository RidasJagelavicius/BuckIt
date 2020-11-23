package com.example.buckit.ui.search;

import android.app.Dialog;
//import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.MyListsActivity;
import com.example.buckit.R;
import com.example.buckit.SharedCode;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    private Button friendButton;
    private Button sendFriendRequestButton;
    private Button cookingButton;
    private Button openAdvicePopup;
    private Button submitAdvice;
    private ImageButton searchButton;
    TextView messageTextView;
    private EditText search_bar;
    private String username;
    private boolean isAdded = false;
    public ArrayList<String> added_users = new ArrayList();

    private Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers1);
        searchButton = findViewById(R.id.search_button);
        search_bar = findViewById(R.id.search_bar);
        if (searchButton != null)
            searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            String input = search_bar.getText().toString();
            if (input.length() == 0) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            } else {
                username = "@" + input;
                Intent intent = new Intent(this, PostActivity2.class);
                Bundle b = new Bundle();
                b.putStringArrayList("added_users", added_users);
                intent.putExtras(b);
                intent.putExtra("username", username); // pass the dictionary to the list
                startActivity(intent);
            }
        }
    }
}