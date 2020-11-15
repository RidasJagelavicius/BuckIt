package com.example.buckit;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.example.buckit.SharedCode.dpToPx;

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
