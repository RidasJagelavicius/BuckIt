package com.example.buckit;

import android.app.Dialog;
//import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

//    Context context = this;
    //EditText input;
    //Button post_advice_button;
    //private Dialog popup;

//    final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
//    LayoutInflater inflater = this.getLayoutInflater();
//    View dialogView = inflater.inflate(R.layout.custom_dialog, null);

    //final EditText editText = (EditText) findViewById(R.id.edit_advice);
//    Button button1 = (Button) dialogView.findViewById(R.id.submit_advice_button);
//    private Button friendButton = (Button) dialogView.findViewById(R.id.friend_button);
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
    private ArrayList<String> added_users = new ArrayList();


    private Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers1);
        popup = new Dialog(this);

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
                setContentView(R.layout.activity_addusers2);
                friendButton = findViewById(R.id.friend_button);
                friendButton.setOnClickListener(this);
                TextView username2 = findViewById(R.id.username2);
                username2.setText(username);
                friendButton.setText(username);

                isAdded = false;
                for (int i = 0; i < added_users.size(); i++) {
                    if (added_users.get(i).equals(username)) {
                        isAdded = true;
                        break;
                    }
                }
            }
        } else if (v.getId() == R.id.friend_button) {
            if (!isAdded) {
                setContentView(R.layout.activity_addusers3);
                TextView username3 = findViewById(R.id.username3);
                username3.setText(username);
                sendFriendRequestButton = findViewById(R.id.send_friend_request_button);
                sendFriendRequestButton.setOnClickListener(this);
            } else {
                added_users.add(username);
                Toast.makeText(this, "Friend request accepted.", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_addusers4);
            }
        } else if (v.getId() == R.id.send_friend_request_button) {
            setContentView(R.layout.activity_addusers4);
            TextView username4 = findViewById(R.id.username4);
            username4.setText(username);
            cookingButton = findViewById(R.id.cooking_button);
            cookingButton.setOnClickListener(this);
        } else if (v.getId() == R.id.cooking_button) {
            setContentView(R.layout.activity_friend_list);
            openAdvicePopup = findViewById(R.id.post_advice_button);
            openAdvicePopup.setOnClickListener(this);
            TextView listName = findViewById(R.id.listName);
            listName.setText(username + "'s list");
        } else if (v.getId() == R.id.post_advice_button) {
            adviceInput();
            openAdvicePopup = findViewById(R.id.post_advice_button);
            openAdvicePopup.setOnClickListener(this);
            TextView listName = findViewById(R.id.listName);
            listName.setText(username + "'s list");
        }
    }

    public void adviceInput() {
        assert (popup != null);
        // Create the dialog that asks user to give advice
        popup.setContentView(R.layout.popup_post_advice);
        final EditText editText = (EditText) popup.findViewById(R.id.edit_advice);
        Button btnCreate = (Button) popup.findViewById(R.id.submit_advice_button);

        // By default, show the popup
        popup.show();

        //Once name list, create a new list
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String innerText = editText.getText().toString();

                // Make sure that the name for the list is valid
                if (innerText.length() > 0) {
                    // Insert advice programmatically
                    LinearLayout advice_container = findViewById(R.id.advice_container);
                    TextView advice = new TextView(PostActivity.this);
                    advice.setText(innerText);
                    advice.setBackgroundColor(getResources().getColor(R.color.superLightPurple));
                    advice.setTypeface(Typeface.DEFAULT_BOLD);
                    advice.setPadding(SharedCode.dpToPx(10), SharedCode.dpToPx(10), SharedCode.dpToPx(10), SharedCode.dpToPx(10));
                    advice.setGravity(Gravity.CENTER);
                    advice_container.addView(advice);

                    popup.dismiss();
                    Toast.makeText(PostActivity.this, "Successfully posted advice", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostActivity.this, "Invalid input, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}