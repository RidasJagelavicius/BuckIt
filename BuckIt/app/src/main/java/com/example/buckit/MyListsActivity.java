package com.example.buckit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class MyListsActivity extends AppCompatActivity {

    private TextView bucket;
    private JSONArray lists = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylists);

        bucket = (TextView) findViewById(R.id.bucketName);

        // Get name and lists passed from bucket
        try {
            // Have bucket name at top
            String name = getIntent().getStringExtra("name");
            bucket.setText(name);

            // Also grab lists
            lists = new JSONArray(getIntent().getStringExtra("lists"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
