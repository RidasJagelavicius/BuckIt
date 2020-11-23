package com.example.buckit.ui.inspirationFeed;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

public class Kind2List extends AppCompatActivity implements View.OnClickListener {
    private Button add;
    private Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kind2_list); //sets up page with list information

        add = (Button) findViewById(R.id.addinspolist); //button to add to user's buckets
        add.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addinspolist){
            popup.setContentView(R.layout.add_list_popup);
            popup.show();

            /*final EditText editText = (EditText) popup.findViewById(R.id.popupListName);
            Button btnCreate = (Button) popup.findViewById(R.id.popupCreateList);

            // By default, show the popup
            popup.show();

            // Once name list, create a new list
            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String innerText = editText.getText().toString();

                    // Make sure that the name for the list is valid
                    if (innerText.length() > 0) {
                        // Insert the list
                        insertList(innerText, true);

                        // Close the popup
                        popup.dismiss();
                    }
                }*/
        }
    }
}
