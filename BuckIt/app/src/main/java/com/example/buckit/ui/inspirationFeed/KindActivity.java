package com.example.buckit.ui.inspirationFeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

public class KindActivity extends AppCompatActivity implements View.OnClickListener {
    private Button donations;
    private Button volunteering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kind_layout);

        donations = (Button) findViewById(R.id.kind1_list);
        donations.setOnClickListener(this);

        volunteering = (Button) findViewById(R.id.kind2_list);
        volunteering.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.kind1_list){
            Intent intent = new Intent(this, Kind1List.class);
            startActivity(intent);
        } else if (v.getId() == R.id.kind2_list){
            Intent intent = new Intent(this, Kind2List.class);
            startActivity(intent);
        }
    }
}
