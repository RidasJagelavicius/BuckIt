package com.example.buckit.ui.inspirationFeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

public class CookActivity extends AppCompatActivity implements View.OnClickListener {
    private Button choco_cookie;
    private Button fried_rice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cook_layout);

        choco_cookie = (Button) findViewById(R.id.cook1_list);
        choco_cookie.setOnClickListener(this);

        fried_rice = (Button) findViewById(R.id.cook2_list);
        fried_rice.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cook1_list){
            Intent intent = new Intent(this, Cook1List.class);
            startActivity(intent);
        } else if (v.getId() == R.id.cook2_list){
            Intent intent = new Intent(this, Cook2List.class);
            startActivity(intent);
        }
    }

}
