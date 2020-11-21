package com.example.buckit.ui.inspirationFeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

public class BucketWithInspo extends AppCompatActivity implements View.OnClickListener {
    Button florida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_bucket_with_inspo); //sets up page with list information

        florida = (Button) findViewById(R.id.florida_travel);
        florida.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), FloridaInBucket.class);
        startActivity(intent);
    }
}
