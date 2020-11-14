package com.example.buckit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.buckit.SharedCode.dpToPx;

public class MyListsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bucket;
    private JSONArray lists = null;
    private LinearLayout listContainer;
    private ImageButton newListButton;
    private Dialog popup;
    private JSONObject listMaster = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylists);

        bucket = (TextView) findViewById(R.id.bucketName);
        popup = new Dialog(this);

        // Get name and lists passed from bucket
        try {
            // Have bucket name at top
            String dictionary = getIntent().getStringExtra("dict");
            JSONObject dict = new JSONObject(dictionary);
            String name = dict.getString("name");
            bucket.setText(name);

            // Also grab lists
            lists = new JSONArray(dict.getString("lists"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Initialize other components
        listContainer = (LinearLayout) findViewById(R.id.listContainer);
        newListButton = (ImageButton) findViewById(R.id.addList);
        newListButton.setOnClickListener(this);

        // Since the program begins with no lists,
        // tell user to make one
        TextView addListText = new TextView(this);
        LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        theseParams.setMargins(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(30)); // set size in DP
        addListText.setLayoutParams(theseParams);
        addListText.setGravity(Gravity.CENTER);
        addListText.setText("Create your first list by pressing the \"+\" button below");
        addListText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        addListText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // set size in SP
        addListText.setId(R.id.backgroundAddList);

        // Add it to the container
        listContainer.addView(addListText);
    }

    @Override
    public void onClick(View v) {
        int myid = v.getId();

        // See if need to create a new list
        if (myid == R.id.addList) {
            createList();
        } else if (listMaster != null) {
            // See if it's a bucket
            String listID = Integer.toString(myid);
            if (listMaster.has(listID)) {
                // If it is, start a new Activity and pass the array of list ID's to it
                try {
                    String dictionary = listMaster.getString(listID);

                    // Start the activity that shows the lists
                    Intent intent = new Intent(this, MyListsActivity.class);
                    intent.putExtra("dict", dictionary); // pass the dictionary to the list
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    // Open a popup for user to type in name of new list, then calls insertList()
    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createList() {
        assert(popup != null);
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.new_list_popup);
        final EditText editText = (EditText) popup.findViewById(R.id.popupListName);
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
                    insertList(innerText);

                    // Close the popup
                    popup.dismiss();
                }
            }
        });
    }

    // Given the name for the list, actually inserts the list
    // Also removes the background "Create list" if it's the first list
    // TODO: Check if list with name already exists
    public void insertList(String name) {
        // uppercase name here so later store it as uppercase in JSON
        name = name.toUpperCase();

        // A list will just be a styled button or something
        Button list = new Button(this);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        listParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        list.setLayoutParams(listParams);
        list.setText(name);
        list.setTransformationMethod(null); // removes the ALL-caps

        int listID = View.generateViewId();
        list.setId(listID);

        // Remove the "Create new list" from the list container IF it's still there
        if (listContainer.findViewById(R.id.backgroundAddList) != null)
            listContainer.removeAllViews();

        // Give the button a listener so clicking on it opens its lists
        list.setOnClickListener(this);

        // Insert bucket into bucket container
        if(list.getParent() != null) {
            ((ViewGroup)list.getParent()).removeView(list); // fix for some weird error
        }
        listContainer.addView(list);

        // Create JSON to represent bucket's lists
        createListJSON(listID, name);
    }

    // Creates a JSON that looks like this
    /*
        {
            <listID 1>: {
                                listID : "",
                                name : "",
                                privacy : "",
                                collaborators : "",
                                photos : "",
                                items : []
                          },
            <listID 2>: {
                              ...
                          }
        }

        * Appends to directory if already exists
        * By default, array of list ID's is empty

        Ref:
            Store and Retrieve: https://stackoverflow.com/questions/40168601/android-how-to-save-json-data-in-a-file-and-retrieve-it
     */
    public void createListJSON(int listID, String listName) {
        // Get the master directory if it exists, otherwise create one
        boolean masterExists = SharedCode.fileExists(this, "lists.json");
        // Read if exists
        if (masterExists) {
            String jsonString = SharedCode.read(this, "lists.json");
            try {
                listMaster = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read list mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = SharedCode.create(this, "lists.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                String jsonString = SharedCode.read(this, "lists.json");
                try {
                    listMaster = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }

        // Now that have the master JSON, insert a record from bucketID to an array
        String toJSON = "{ \"listID\" : " + listID + "\", \"name\" : \"" + listName + "\", \"privacy\" : \"private\", \"collaborators\" : \"\", \"photos\" : \"\", \"items\": []}";
        try {
            JSONObject listStuff = new JSONObject(toJSON);

            // Insert the empty lists array into the bucket
            listMaster.put(Integer.toString(listID), listStuff);
            boolean fileCreated = SharedCode.create(this, "lists.json", listMaster.toString());
            if (!fileCreated) {
                Log.e("JSON", "Failed to append to master JSON file");
                return;
            }
            Log.v("JSON", "Successfully inserted list mapping");
        } catch (JSONException e) {
            Log.v("JSON", "Failed to create list mapping");
            e.printStackTrace();
        }
    }
}
