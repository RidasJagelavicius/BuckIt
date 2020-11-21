package com.example.buckit.ui.inspirationFeed;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buckit.R;

import static android.widget.Toast.*;

public class Travel1List extends AppCompatActivity {
    private Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel1_list); //sets up page with list information

        popup = new Dialog(this); //dialog to ask which bucket to add list
    }

    public void showPopup(View v){
        popup.setContentView(R.layout.add_list_popup);
        popup.show();
        Button travel;
        Button cooking;

        travel = (Button) popup.findViewById(R.id.add_to_travel);
        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BucketWithInspo.class);
                Toast.makeText(v.getContext(), "List added to Travel Bucket!", LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        cooking = (Button) popup.findViewById(R.id.add_to_cooking);

    }

}
