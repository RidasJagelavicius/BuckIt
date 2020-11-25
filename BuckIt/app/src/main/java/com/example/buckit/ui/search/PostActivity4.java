package com.example.buckit.ui.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.MyListsActivity;
import com.example.buckit.R;
import com.example.buckit.SharedCode;

import java.util.ArrayList;

public class PostActivity4 extends AppCompatActivity implements View.OnClickListener {
    private Button openAdvicePopup;
    private TextView listName;
    private TextView friendName;
    private Dialog popup;
    private Context thisContext;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        popup = new Dialog(this);
        thisContext = this;
        username = getIntent().getStringExtra("username");
        openAdvicePopup = findViewById(R.id.post_advice_button);
        openAdvicePopup.setOnClickListener(this);
        listName = findViewById(R.id.listName);
        listName.setText("COOKING");

        friendName = findViewById(R.id.friendName);
        friendName.setText(username);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post_advice_button) {
            adviceInput();
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
                    TextView advice = new TextView(thisContext);
                    advice.setText(innerText);
                    advice.setBackgroundColor(getResources().getColor(R.color.superLightPurple));
                    advice.setTypeface(Typeface.DEFAULT_BOLD);
                    advice.setPadding(SharedCode.dpToPx(10), SharedCode.dpToPx(10), SharedCode.dpToPx(10), SharedCode.dpToPx(10));
                    advice.setGravity(Gravity.CENTER);
                    advice_container.addView(advice);

                    popup.dismiss();
                    Toast.makeText(thisContext, "Successfully posted advice", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(thisContext, "Invalid input, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}