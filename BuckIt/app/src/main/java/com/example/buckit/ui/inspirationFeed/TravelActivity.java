package com.example.buckit.ui.inspirationFeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

public class TravelActivity extends AppCompatActivity implements View.OnClickListener {

    private Button florida;
    private Button india;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_layout);

        florida = (Button) findViewById(R.id.travel1_list);
        florida.setOnClickListener(this);

        india = (Button) findViewById(R.id.travel2_list);
        india.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.travel1_list){
            Intent intent = new Intent(this, Travel1List.class);
            startActivity(intent);
        } else if (v.getId() == R.id.travel2_list){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unable to load list. Please try again later.");

            // Set Alert Title
            builder.setTitle("Error!");

            builder.setCancelable(true);

            //Set Ok button to dismiss popup
            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();
        }
    }
}
