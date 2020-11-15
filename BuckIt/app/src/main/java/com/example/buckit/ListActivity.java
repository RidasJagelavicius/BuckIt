package com.example.buckit;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity {

    private TextView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list = (TextView) findViewById(R.id.listName);
        // TODO: Populate the other stuff from the JSON

        // Get name and lists passed from bucket
        try {
            // Have bucket name at top
            String dictionary = getIntent().getStringExtra("dict");
            JSONObject dict = new JSONObject(dictionary);
            String name = dict.getString("name");
            list.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
